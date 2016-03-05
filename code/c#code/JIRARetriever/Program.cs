using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;

namespace JIRARetriever
{
    class Program
    {
        static void Main(string[] args)
        {
            try
            {
                Uri uri;
                int startAt = 0;
                Boolean process = true;
                //String sFields = "key,id,self,summary,description,status";
                String sFields = "id,summary,description";

                StreamWriter jsonDERBY = new StreamWriter(@"derby_issues.json", false);
                StreamWriter jsonOFBIZ = new StreamWriter(@"ofbiz_issues.json", false);

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
    }
}
