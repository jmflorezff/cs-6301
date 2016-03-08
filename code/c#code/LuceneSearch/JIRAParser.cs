using Lucene.Net.Analysis;
using Lucene.Net.Documents;
using Lucene.Net.Index;
using Lucene.Net.Store;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Text.RegularExpressions;

namespace LuceneSearch
{
    class JIRAParser
    {
        public void simplifyJiraBatch()
        {
            try
            {
                String sDerbyJSON = AppDomain.CurrentDomain.BaseDirectory + @"jira\derby_issues.json";
                String sOfbizJSON = AppDomain.CurrentDomain.BaseDirectory + @"jira\ofbiz_issues.json";

                StreamWriter jsonDERBY = new StreamWriter(@"jira\derby_issues_filtered.json", false);
                StreamWriter jsonOFBIZ = new StreamWriter(@"jira\ofbiz_issues_filtered.json", false);


                List<JIRABatch> results = new List<JIRABatch>();

                String line;
                System.IO.StreamReader indexFile = new System.IO.StreamReader(sDerbyJSON);
                while ((line = indexFile.ReadLine()) != null)
                {
                    JIRABatch result = JsonConvert.DeserializeObject<JIRABatch>(line);
                    results.Add(result);
                }
                indexFile.Close();

                foreach (JIRABatch batch in results)
                {
                    foreach (Bug issue in batch.issues)
                    {
                        if (issue.fields.description == null) issue.fields.description = "";
                        if (issue.fields.summary == null) issue.fields.summary = "";
                        issue.fields.description = removeJunk(issue.fields.description.ToString());
                        issue.fields.summary = removeJunk(issue.fields.summary.ToString());
                        jsonDERBY.WriteLine("{ \"id\" : \"" + issue.id + "\", \"key\" : \"" + issue.key + "\", \"title\" : [" + issue.fields.summary + "], \"description\" : [" + issue.fields.description + "]}");
                    }
                }

                line = "";

                jsonDERBY.Close();

                results.Clear();

                indexFile = new System.IO.StreamReader(sOfbizJSON);
                while ((line = indexFile.ReadLine()) != null)
                {
                    JIRABatch result = JsonConvert.DeserializeObject<JIRABatch>(line);
                    results.Add(result);
                }
                indexFile.Close();

                foreach (JIRABatch batch in results)
                {
                    foreach (Bug issue in batch.issues)
                    {
                        if (issue.fields.description == null) issue.fields.description = "";
                        if (issue.fields.summary == null) issue.fields.summary = "";
                        issue.fields.description = removeJunk(issue.fields.description.ToString());
                        issue.fields.summary = removeJunk(issue.fields.summary.ToString());
                        jsonOFBIZ.WriteLine("{ \"id\" : \"" + issue.id + "\", \"key\" : \"" + issue.key + "\", \"title\" : [" + issue.fields.summary + "], \"description\" : [" + issue.fields.description + "]}");
                    }
                }

                line = "";
                results.Clear();
                jsonOFBIZ.Close();
            }
            catch (Exception ex)
            {
                string s = ex.ToString();
            }
        }

        public void addToDictionary()
        {
            String derbyJson = AppDomain.CurrentDomain.BaseDirectory + @"jira\derby_issues_filtered.json";
            String ofbizJson = AppDomain.CurrentDomain.BaseDirectory + @"jira\ofbiz_issues_filtered.json";

            List<Issue> issuesDerby = new List<Issue>();
            List<Issue> issuesOfbiz = new List<Issue>();

            String line;
            System.IO.StreamReader indexFileDerby = new System.IO.StreamReader(derbyJson);
            System.IO.StreamReader indexFileOfbiz = new System.IO.StreamReader(ofbizJson);
            //System.IO.StreamReader indexFile = new System.IO.StreamReader(indexRhinoFile);
            while ((line = indexFileDerby.ReadLine()) != null)
            {
                Issue issue = JsonConvert.DeserializeObject<Issue>(line);
                issuesDerby.Add(issue);
            }
            indexFileDerby.Close();

            while ((line = indexFileOfbiz.ReadLine()) != null)
            {
                Issue issue = JsonConvert.DeserializeObject<Issue>(line);
                issuesOfbiz.Add(issue);
            }
            indexFileOfbiz.Close();

            Lucene.Net.Store.Directory indexDirectory = FSDirectory.Open(@"c:\temp_corpus"); //System.IO.Directory.GetCurrentDirectory());
                                                                                             //Lucene.Net.Store.Directory indexDirectory = FSDirectory.Open(@"c:\TEMP_RHINO2");

            Analyzer analyzer = new SimpleAnalyzer();
            //Analyzer analyzer = new StopAnalyzer(Lucene.Net.Util.Version.LUCENE_30);
            //Analyzer analyzer = new WhitespaceAnalyzer();
            // Analyzer analyzer = new StandardAnalyzer(Lucene.Net.Util.Version.LUCENE_30);

            //CREATE INDEX

            IndexWriterConfig config = new IndexWriterConfig();
            IndexWriter writer = new IndexWriter(indexDirectory, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);

            //For each line in LUCENE-DOCUMENTS
            foreach (Issue d in issuesDerby)
            {
                Document doc;// = new Document();
                doc = d.ToDocument();
                //doc.Add(new Field("name", d.name, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
                //doc.Add(new Field("id", d.id, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
                //doc.Add(new Field("file_name", d.file_name, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
                //doc.Add(new Field("tokens", d.tokens.ToString(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
                writer.AddDocument(doc);
            }

            writer.Optimize();
            writer.Commit();
            writer.Dispose();

            foreach (Issue d in issuesOfbiz)
            {
                Document doc;// = new Document();
                doc = d.ToDocument();
                //doc.Add(new Field("name", d.name, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
                //doc.Add(new Field("id", d.id, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
                //doc.Add(new Field("file_name", d.file_name, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
                //doc.Add(new Field("tokens", d.tokens.ToString(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
                writer.AddDocument(doc);
            }

            writer.Optimize();
            writer.Commit();
            writer.Dispose();
        }

        private void retrieveJIRAReports()
        {
            try
            {
                Uri uri;
                int startAt = 0;
                Boolean process = true;
                //String sFields = "key,id,self,summary,description,status";
                String sFields = "id,summary,description";

                StreamWriter jsonDERBY = new StreamWriter(@"jira\derby_issues.json", false);
                StreamWriter jsonOFBIZ = new StreamWriter(@"jira\ofbiz_issues.json", false);

                String project = "DERBY";
                String response = "";
                WebClient serviceRequest = new WebClient();

                Console.WriteLine("Retrieving DERBY issues");

                while (process)
                {
                    uri = new Uri(String.Format("https://issues.apache.org/jira/rest/api/2/search?jql=project={0}&fields={1}&startAt={2}&maxResults=-1", project, sFields, startAt.ToString()));
                    response = serviceRequest.DownloadString(uri);
                    jsonDERBY.WriteLine(response);
                    startAt += 100;
                    if (startAt > 6900) process = false;
                    Console.WriteLine("Position: " + startAt.ToString());
                }
                jsonDERBY.Close();

                startAt = 0;
                response = "";
                process = true;
                project = "OFBIZ";

                Console.WriteLine("Retrieving OFBIZ issues");

                while (process)
                {
                    uri = new Uri(String.Format("https://issues.apache.org/jira/rest/api/2/search?jql=project={0}&fields={1}&startAt={2}&maxResults=-1", project, sFields, startAt.ToString()));
                    response = serviceRequest.DownloadString(uri);
                    jsonOFBIZ.WriteLine(response);
                    startAt += 100;
                    if (startAt > 7000) process = false;
                    Console.WriteLine("Position: " + startAt.ToString());
                }
                jsonOFBIZ.Close();
            }
            catch (Exception ex)
            {
                Console.WriteLine("ERROR: " + ex.ToString());
            }
        }

        private String removeJunk(String sValue)
        {

            String sOriginal = sValue;

            //Remove HTML and XML tags "<.*?>"
            sValue = Regex.Replace(sValue, "<[^>]+>[^<]+</[^>]+>", " ", RegexOptions.IgnoreCase);

            //Remove URLs
            sValue = Regex.Replace(sValue, @"http[^\s]+", " ", RegexOptions.IgnoreCase);

            //Remove non ascii chars
            sValue = Encoding.ASCII.GetString(
            Encoding.Convert(
                Encoding.UTF8,
                Encoding.GetEncoding(
                    Encoding.ASCII.EncodingName,
                    new EncoderReplacementFallback(string.Empty),
                    new DecoderExceptionFallback()
                    ),
                Encoding.UTF8.GetBytes(sValue)
            ));

            //Remove white spaces
            sValue = Regex.Replace(sValue, @"\s", " ", RegexOptions.IgnoreCase).Trim();

            //Remove api classes
            String[] java_api_classes = File.ReadAllLines(@"wordlists\java_api_classes.txt");

            foreach (String word in java_api_classes)
            {
                sValue = Regex.Replace(sValue, @"\b" + word + @"\b", " ", RegexOptions.IgnoreCase); // @"\b" + word  + @"\b", " ");
            }

            //Remove java keywords
            String[] java_keywords = File.ReadAllLines(@"wordlists\java_keywords.txt");

            foreach (String word in java_keywords)
            {
                sValue = Regex.Replace(sValue, @"\b" + word + @"\b", " ", RegexOptions.IgnoreCase);
            }

            //Remove stop words
            String[] stop_words = File.ReadAllLines(@"wordlists\stop_words.txt");


            foreach (String word in stop_words)
            {
                sValue = Regex.Replace(sValue, @"\b" + word + @"\b", " ", RegexOptions.IgnoreCase);
            }

            //Split CamelCase
            sValue = System.Text.RegularExpressions.Regex.Replace(sValue, "([A-Z])", " $1", System.Text.RegularExpressions.RegexOptions.Compiled);

            //Remove numbers
            sValue = Regex.Replace(sValue, @"[\d]", " ", RegexOptions.IgnoreCase);

            //Remove words with less than 2 charaters
            sValue = Regex.Replace(sValue, @"\b\w{1,2}\b", " ", RegexOptions.IgnoreCase);

            //Lower case
            sValue = sValue.ToLower();

            //Remove comments
            String blockComments = @"/\*(.*?)\*/";
            sValue = Regex.Replace(sValue, blockComments, " ", RegexOptions.IgnoreCase);

            //String lineComments = @"//(.*?)\r?\n";
            //sValue = Regex.Replace(sValue, lineComments, " ");

            //Remove Programming Tokens
            sValue = sValue.Replace("[", " ");
            sValue = sValue.Replace("]", " ");
            sValue = sValue.Replace("=", " ");
            sValue = sValue.Replace("&", " ");
            sValue = sValue.Replace("$", " ");
            sValue = sValue.Replace("|", " ");
            sValue = sValue.Replace(";", " ");
            sValue = sValue.Replace(",", " ");
            sValue = sValue.Replace(":", " ");
            sValue = sValue.Replace("<", " ");
            sValue = sValue.Replace(">", " ");
            sValue = sValue.Replace("!", " ");
            sValue = sValue.Replace("#", " ");
            sValue = sValue.Replace("'", " ");
            sValue = sValue.Replace("\"", " ");
            sValue = sValue.Replace(@"\", " ");
            sValue = sValue.Replace("#", " ");
            sValue = sValue.Replace("%", " ");
            sValue = sValue.Replace("=", " ");

            //Jira specific
            sValue = sValue.Replace("h1.", " ");
            sValue = sValue.Replace("h2.", " ");
            sValue = sValue.Replace("h3.", " ");
            sValue = sValue.Replace("h4.", " ");
            sValue = sValue.Replace("h5.", " ");
            sValue = sValue.Replace("h6.", " ");
            sValue = sValue.Replace("bq.", " ");
            sValue = sValue.Replace("*", " ");
            sValue = sValue.Replace("_", " ");
            sValue = sValue.Replace("?", " ");
            sValue = sValue.Replace("_", " ");
            sValue = sValue.Replace("-deleted-", "deleted ");
            sValue = sValue.Replace("+", " ");
            sValue = sValue.Replace("^", " ");
            sValue = sValue.Replace("{", " ");
            sValue = sValue.Replace("}", " ");
            sValue = sValue.Replace("~", " ");
            sValue = sValue.Replace("(", " ");
            sValue = sValue.Replace(")", " ");
            sValue = sValue.Replace("----", " ");
            sValue = sValue.Replace("---", " ");
            sValue = sValue.Replace("--", " ");
            sValue = sValue.Replace(@"\\", " ");
            sValue = sValue.Replace("(empty line)", " ");
            sValue = sValue.Replace("(y)", " ");
            sValue = sValue.Replace("(n)", " ");
            sValue = sValue.Replace("(i)", " ");
            sValue = sValue.Replace("(/)", " ");
            sValue = sValue.Replace("(x)", " ");
            sValue = sValue.Replace("(!)", " ");
            sValue = sValue.Replace("(+)", " ");
            sValue = sValue.Replace("(-)", " ");
            sValue = sValue.Replace("(?)", " ");
            sValue = sValue.Replace("(on)", " ");
            sValue = sValue.Replace("(off)", " ");
            sValue = sValue.Replace("(*)", " ");
            sValue = sValue.Replace("(*r)", " ");
            sValue = sValue.Replace("(*g)", " ");
            sValue = sValue.Replace("(*b)", " ");
            sValue = sValue.Replace("(*y)", " ");
            sValue = sValue.Replace("(flag)", " ");
            sValue = sValue.Replace("(flagoff)", " ");
            sValue = sValue.Replace(":)", " ");
            sValue = sValue.Replace(":(", " ");
            sValue = sValue.Replace(":P", " ");
            sValue = sValue.Replace(":D", " ");
            sValue = sValue.Replace(";)", " ");

            //Remove double spaces
            String[] temp = sValue.Split(new[] { ' ' }, StringSplitOptions.RemoveEmptyEntries);
            sValue = "";
            for (Int16 i = 0; i < temp.Length; i++)
            {
                temp[i] = temp[i].Trim();
                if (temp[i].Length > 0) if (temp[i][0] == '-') temp[i] = temp[i].Remove(0, 1);
                if (temp[i].Length > 0) if (temp[i][0] == '/') temp[i] = temp[i].Remove(0, 1);
                if (temp[i].Length > 0) if (temp[i][temp[i].Length - 1] == '-') temp[i] = temp[i].Remove(temp[i].Length - 1, 1);
                if (temp[i].Length > 0) if (temp[i][temp[i].Length - 1] == '/') temp[i] = temp[i].Remove(temp[i].Length - 1, 1);
                sValue += temp[i] + " ";
            }

            //Split in phrases
            temp = sValue.Split(new[] { ". " }, StringSplitOptions.RemoveEmptyEntries);
            sValue = "";
            for (Int16 i = 0; i < temp.Length; i++)
            {
                if (temp[i] != " ")
                {
                    if (temp[i].Length > 2)
                    {
                        sValue += " \"" + temp[i].Replace('.', ' ').Trim() + "\", ";
                    }
                }
            }

            sValue = sValue.Trim();
            if (sValue.Length > 0)
            {
                sValue = sValue.Remove(sValue.Length - 1);
                return sValue;
            }
            else
            {
                sOriginal.Trim();
                return " \"\" ";
            }
        }
    }
}

