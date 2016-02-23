using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using Lucene.Net.QueryParsers;
using Lucene.Net.Search;
using Lucene.Net.Analysis;
using Lucene.Net.Analysis.Standard;
using Lucene.Net.Index;
using Lucene.Net.Store;
using Lucene.Net.Documents;
using Newtonsoft.Json;
using System.IO;
/*
 * LUCENE 3.0.3
 * BASIC SEARCH
 * http://www.avajava.com/tutorials/lessons/how-do-i-use-lucene-to-index-and-search-text-files.html
 * QUERY SYNTAX
 * http://www.lucenetutorial.com/lucene-query-syntax.html
 * JSON FILES
 * http://ignaciosuay.com/getting-started-with-lucene-and-json-indexing/
 * LUCENE OBJECT MAPPING
 * https://github.com/rokeller/Lucene.Net.ObjectMapping
 * FIELDS FOR QUERIES
 * "id" "title_tokens" "description_tokens" "doc_ids"
 * 
 * FIELDS FOR METHODS
 * "name" "id" "file_name" "tokens" 
 * 
 * */
namespace LuceneSearch
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            try
            {
                String indexRhinoFile = System.IO.Directory.GetCurrentDirectory() + @"\rhino-documents.json";
                String queryRhinoFile = System.IO.Directory.GetCurrentDirectory() + @"\rhino-queries.json";
                String indexLuceneFile = System.IO.Directory.GetCurrentDirectory() + @"\lucene-documents.json";
                String queryLuceneFile = System.IO.Directory.GetCurrentDirectory() + @"\lucene-queries.json";

                List<indexDocument> results = new List<indexDocument>();
                List<indexQuery> queries = new List<indexQuery>();

                String line;
                System.IO.StreamReader indexFile = new System.IO.StreamReader(indexLuceneFile);
                //System.IO.StreamReader indexFile = new System.IO.StreamReader(indexRhinoFile);
                while ((line = indexFile.ReadLine()) != null)
                {
                    indexDocument result = JsonConvert.DeserializeObject<indexDocument>(line);
                    results.Add(result);
                }
                indexFile.Close();

                line = "";
                System.IO.StreamReader queryFile = new System.IO.StreamReader(queryLuceneFile);
                //System.IO.StreamReader queryFile = new System.IO.StreamReader(queryRhinoFile);
                while ((line = queryFile.ReadLine()) != null)
                {
                    indexQuery query = JsonConvert.DeserializeObject<indexQuery>(line);
                    queries.Add(query);
                }
                queryFile.Close();

                Lucene.Net.Store.Directory indexDirectory = FSDirectory.Open(@"c:\TEMP_LUCENE2");//System.IO.Directory.GetCurrentDirectory());
                //Lucene.Net.Store.Directory indexDirectory = FSDirectory.Open(@"c:\TEMP_RHINO2");

                Analyzer analyzer = new StandardAnalyzer(Lucene.Net.Util.Version.LUCENE_30);
                //CREATE INDEX

                IndexWriterConfig config = new IndexWriterConfig();
                IndexWriter writer = new IndexWriter(indexDirectory, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);

                //For each line in LUCENE-DOCUMENTS
                foreach (indexDocument d in results)
                {
                    Document doc;// = new Document();
                    doc = d.ToDocument();
                    //doc.Add(new Field("name", d.name, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
                    //doc.Add(new Field("id", d.id, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
                    //doc.Add(new Field("file_name", d.file_name, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
                    //doc.Add(new Field("tokens", d.tokens.ToString(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
                    writer.AddDocument(doc);
                }

                //writer.Optimize();
                writer.Commit();
                writer.Dispose();

                //System.IO.StreamWriter resultsFile = new System.IO.StreamWriter(@"C:\JSON_RESULTS\lucene_results.json");
                //File.Create(@"C:\rhino_results.json");
                
                //System.IO.StreamWriter resultsFile = new System.IO.StreamWriter(@"C:\JSON_RESULTS\rhino_results.json", true);
                //SEARCH
                //For each query
                foreach (indexQuery q in queries)
                {
                    Document query;// = new Document();
                    query = q.ToDocument();
                    //console.writeline("Query " + query.Get("id"));
                    String jsonResult = "{\"query_id\": " + query.Get("id") + ", ";

                    Field[] title_tokens = query.GetFields("title_tokens");
                    Field[] description_tokens = query.GetFields("description_tokens");
                    String title_query = "";
                    String description_query = "";

                    foreach (Field f in title_tokens)
                    {
                        title_query += " " + f.StringValue;
                    }

                    foreach (Field f in description_tokens)
                    {
                        description_query += " " + f.StringValue;
                    }

                    String query1 = title_query + " " + description_query;
                    String query2 = title_query;
                    String query3 = description_query;


                    QueryParser queryParser = new QueryParser(Lucene.Net.Util.Version.LUCENE_30, "tokens", analyzer);
                    Query query1Parsed = queryParser.Parse(query1);
                    Query query2Parsed = queryParser.Parse(query2);
                    Query query3Parsed = queryParser.Parse(query3);


                    int hitsPerPage = 20;

                    //Lucene.Net.Store.Directory directory = FSDirectory.Open(System.IO.Directory.GetCurrentDirectory());
                    IndexReader reader = DirectoryReader.Open(indexDirectory, true);
                    IndexSearcher searcher = new IndexSearcher(reader);
                    //console.writeline("---------------");

                    jsonResult += "\"title_and_description\": [";
                    //console.writeline("Results for query #1 - Tile and Description:");
                    TopDocs docs = searcher.Search(query1Parsed, hitsPerPage);
                    ScoreDoc[] hits = docs.ScoreDocs;
                    for (int i = 0; i < hits.Length; i++)
                    {
                        int docId = hits[i].Doc;
                        Document d = searcher.Doc(docId);
                        //string file_name = d.Get("file_name");
                        //console.writeline("DocId: " + docId.ToString());
                        if (i == hits.Length-1) jsonResult += docId.ToString();
                        else jsonResult += docId.ToString() + ", ";
                    }
                    jsonResult += "], ";
                    //console.writeline("---------------");

                    jsonResult += "\"title_only\": [";
                    //console.writeline("Results for query #2 - Title only:");
                    docs = searcher.Search(query2Parsed, hitsPerPage);
                    hits = docs.ScoreDocs;


                    for (int i = 0; i < hits.Length; i++)
                    {
                        int docId = hits[i].Doc;
                        //Document d = searcher.Doc(docId);
                        //string file_name = d.Get("file_name");
                        //console.writeline("DocId: " + docId.ToString());
                        if (i == hits.Length-1) jsonResult += docId.ToString();
                        else jsonResult += docId.ToString() + ", ";
                    }
                    jsonResult += "], ";
                    //console.writeline("---------------");

                    jsonResult += "\"description_only\": [";
                    //console.writeline("Results for query #3 - Description only:");
                    docs = searcher.Search(query3Parsed, hitsPerPage);
                    hits = docs.ScoreDocs;
                    for (int i = 0; i < hits.Length; i++)
                    {
                        int docId = hits[i].Doc;
                        //Document d = searcher.Doc(docId);
                        //string file_name = d.Get("file_name");
                        //console.writeline("DocId: " + docId.ToString());
                        if (i == hits.Length-1) jsonResult += docId.ToString();
                        else jsonResult += docId.ToString() + ", ";
                    }
                    jsonResult += "]}";
                    //console.writeline("---------------");

                    //resultsFile.WriteLine(jsonResult);
                    Console.WriteLine(jsonResult);
                }
                //resultsFile.Close();
            }
            catch (Exception ex) { MessageBox.Show(ex.ToString()); }

        }
    }
    class indexDocument
    {
        public string name { get; set; }
        public string id { get; set; }
        public string file_name { get; set; }
        public string[] tokens { get; set; }
    }
    class indexQuery
    {
        public string id { get; set; }
        public string[] title_tokens { get; set; }
        public string[] description_tokens { get; set; }
        public string[] doc_ids { get; set; }
    }
}
