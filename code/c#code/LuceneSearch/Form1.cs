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
                String indexRhinoFile = System.IO.Directory.GetCurrentDirectory() + @"\lucene-documents.json";
                String queryRhinoFile = System.IO.Directory.GetCurrentDirectory() + @"\rhino-queries.json";
                String indexLuceneFile = System.IO.Directory.GetCurrentDirectory() + @"\lucene-documents.json";
                String queryLuceneFile = System.IO.Directory.GetCurrentDirectory() + @"\lucene-queries.json";

                List<indexDocument> results = new List<indexDocument>();
                List<indexQuery> queries = new List<indexQuery>();

                String line;
                System.IO.StreamReader indexFile = new System.IO.StreamReader(indexRhinoFile);
                while ((line = indexFile.ReadLine()) != null)
                {
                    indexDocument result = JsonConvert.DeserializeObject<indexDocument>(line);
                    results.Add(result);
                }
                indexFile.Close();

                line = "";
                System.IO.StreamReader queryFile = new System.IO.StreamReader(queryRhinoFile);
                while ((line = queryFile.ReadLine()) != null)
                {
                    indexQuery query = JsonConvert.DeserializeObject<indexQuery>(line);
                    queries.Add(query);
                }
                queryFile.Close();

                Lucene.Net.Store.Directory indexDirectory = FSDirectory.Open(System.IO.Directory.GetCurrentDirectory());

                Analyzer analyzer = new StandardAnalyzer(Lucene.Net.Util.Version.LUCENE_30);
                //CREATE INDEX

                IndexWriterConfig config = new IndexWriterConfig();
                IndexWriter writer = new IndexWriter(indexDirectory, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);

                //For each line in LUCENE-DOCUMENTS
                foreach (indexDocument d in results)
                {
                    Document doc = new Document();
                    doc.Add(new Field("name", d.name, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
                    doc.Add(new Field("id", d.id, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
                    doc.Add(new Field("file_name", d.file_name, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
                    doc.Add(new Field("tokens", d.tokens.ToString(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
                }

                writer.Commit();
                writer.Close();

                //SEARCH
                String querystr = "23";
                QueryParser queryParser = new QueryParser(Lucene.Net.Util.Version.LUCENE_30, "id", analyzer);
                Query q = queryParser.Parse(querystr);

                int hitsPerPage = 10;
                Lucene.Net.Store.Directory directory = FSDirectory.Open(System.IO.Directory.GetCurrentDirectory());
                IndexReader reader = DirectoryReader.Open(directory, true);
                IndexSearcher searcher = new IndexSearcher(reader);

                TopDocs docs = searcher.Search(q, hitsPerPage);
                ScoreDoc[] hits = docs.ScoreDocs;
                for (int i = 0; i < hits.Length; i++)
                {
                    int docId = hits[i].Doc;
                    Document d = searcher.Doc(docId);
                    string s = d.Get("file_name");
                    string s2 = d.Get("tokens");
                }
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
