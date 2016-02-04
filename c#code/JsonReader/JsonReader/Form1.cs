using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;


namespace JsonReader
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {

            process10most("freemind_index.json");
            process10mostInverted("freemind_inverted.json");
            process10most("weka_index.json");
            process10mostInverted("weka_inverted.json");

        }

        private void process10most(String filename)
        {
            StreamReader srReader = new StreamReader(filename);
            string jsonData = srReader.ReadToEnd();

            String[,] arrayWordValue = new String[1, 2];
            List<Item> items = new List<Item>();

            String[] a = jsonData.Split('}');
            String[] c, d;
            for (int i = 0; i < a.Length; i++)
            {
                String[] b = a[i].Split('{');
                if (b.Length == 3)
                {
                    c = b[2].Split(',');
                }
                if (b.Length == 2)
                {
                    c = b[1].Split(',');
                }
                else
                {
                    c = null;
                }
                if (c != null)
                {
                    for (int ii = 0; ii < c.Length; ii++)
                    {
                        d = c[ii].Split(':');

                        Item item = new Item();
                        item.word = d[0].Replace('"', ' ').Replace('{', ' ').Replace('}', ' ').Replace('[', ' ').Replace(']', ' ').Trim();
                        item.times = d[1].Replace('"', ' ').Replace('{', ' ').Replace('}', ' ').Replace('[', ' ').Replace(']', ' ').Trim();
                        items.Add(item);
                    }
                }
            }

            var results = items.OrderByDescending(it => it.times);

            int count = 0;

            print(results, "results_" + filename + ".csv");

            foreach (Item item in results)
            {
                if (count < 20)
                {
                    Console.WriteLine(item.ToString());
                    count++;
                }
            }

            Console.WriteLine("=========================================");
        }
        private void process10mostInverted(String filename)
        {
            StreamReader srReader = new StreamReader(filename);
            string jsonData = srReader.ReadToEnd();

            String[,] arrayWordValue = new String[1, 2];
            List<ItemInverted> items = new List<ItemInverted>();

            String[] a = jsonData.Split(']');
            //String[] c, d;
            for (int i = 0; i < a.Length; i++)
            {
                String[] b = a[i].Split(':');
                if (b.Length == 3)
                {

                }
                if (b.Length == 2)
                {
                    for (int ii = 0; ii < b.Length; ii++)
                    {
                        ItemInverted itemInverted = new ItemInverted();
                        itemInverted.word = b[0].Replace('"', ' ').Replace('{', ' ').Replace('}', ' ').Replace('[', ' ').Replace(']', ' ').Replace(',', ' ').Trim();
                        itemInverted.file = b[1].Split(',')[0].Replace('"', ' ').Replace('{', ' ').Replace('}', ' ').Replace('[', ' ').Replace(']', ' ').Trim();
                        itemInverted.times = b[1].Split(',')[1].Replace('"', ' ').Replace('{', ' ').Replace('}', ' ').Replace('[', ' ').Replace(']', ' ').Trim();
                        items.Add(itemInverted);
                    }
                }



            }

            //var results = items.OrderByDescending(it => it.times);
            //DOCUMENTO
            var results = items.OrderByDescending(wd => wd.word).ThenByDescending(dc => dc.file);

            int count = 0;

            print(results, "results_" + filename + ".csv");

            foreach (ItemInverted item in results)
            {
                if (count < 20)
                {
                    Console.WriteLine(item.ToString());
                    count++;
                }
            }

            Console.WriteLine("=========================================");
        }

        public class Item
        {
            public string word;
            public string times;

            public override string ToString()
            {
                //return "Word : " + this.word + " - Times : " + times;
                return this.word + ", " + times;
            }
        }

        public class ItemInverted
        {
            public string word;
            public string file;
            public string times;


            public override string ToString()
            {
                //return "Word : " + this.word + " File : " + this.file + " - Times : " + times;
                return this.word + ", " + this.file + ", " + times;
            }
        }

        public JObject DeserializeAccounts(string json)
        {
            return (JObject)JsonConvert.DeserializeObject(json);
        }

        public void print(IOrderedEnumerable<Item> items, String filename)
        {
            StreamWriter stWriter = new StreamWriter(filename);
            foreach (Item item in items)
            {
                stWriter.WriteLine(item.ToString());
            }
            stWriter.Close();
        }
        public void print(IOrderedEnumerable<ItemInverted> items, String filename)
        {
            StreamWriter stWriter = new StreamWriter(filename);
            int count = 1;
            foreach (ItemInverted item in items)
            {
                if ((count % 2) == 1) stWriter.WriteLine(item.ToString());
                count++;
            }
            stWriter.Close();
        }

        public void get3Most(IOrderedQueryable<ItemInverted> items) {
        }

    }
}
