using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace LuceneSearch
{
    class JIRABatch
    {
        public string expand { get; set; }
        public string startAt { get; set; }
        public string maxResults { get; set; }
        public string total { get; set; }
        public List<Bug> issues { get; set; }
    }
    class Bug
    {
        public string expand { get; set; }
        public string id { get; set; }
        public string self { get; set; }
        public string key { get; set; }
        public Fields fields { get; set; }
    }
    class Fields
    {
        public string summary { get; set; }
        public string description { get; set; }
    }

    class Issue
    {
        public string id { get; set; }
        public string key { get; set; }
        public List<Phrase> title { get; set; }
        public List<Phrase> description { get; set; }
    }
    class Phrase
    {
           public string phrase { get; set; }
    }
}
