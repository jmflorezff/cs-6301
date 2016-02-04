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

            process("freemind_index.json");
            //process("freemind_inverted.json");
            process("weka_index.json");
            //process("weka_inverted.json");

        }

        private void process(String filename) {
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
                        item.word = d[0].Replace('"', ' ').Trim();
                        item.times = d[1].Trim();
                        items.Add(item);
                    }
                }
            }

            var results = items.OrderByDescending(it => it.times);

            int count = 0;
            foreach (Item item in results)
            {
                if (count < 20)
                {
                    Console.WriteLine(item.word + "==" + item.times);
                    count++;
                }
            }

            Console.WriteLine("=========================================");
        }

        public class Item
        {
            public string word;
            public string times;
        }

        public JObject DeserializeAccounts(string json)
        {
            return (JObject)JsonConvert.DeserializeObject(json);
        }

    }
}
