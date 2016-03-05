namespace LuceneSearch
{
    partial class Form1
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.btnAssignment2 = new System.Windows.Forms.Button();
            this.btnAssignment3 = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // btnAssignment2
            // 
            this.btnAssignment2.Location = new System.Drawing.Point(46, 27);
            this.btnAssignment2.Name = "btnAssignment2";
            this.btnAssignment2.Size = new System.Drawing.Size(142, 23);
            this.btnAssignment2.TabIndex = 0;
            this.btnAssignment2.Text = "Rhino and Lucene";
            this.btnAssignment2.UseVisualStyleBackColor = true;
            // 
            // btnAssignment3
            // 
            this.btnAssignment3.Location = new System.Drawing.Point(46, 85);
            this.btnAssignment3.Name = "btnAssignment3";
            this.btnAssignment3.Size = new System.Drawing.Size(142, 23);
            this.btnAssignment3.TabIndex = 1;
            this.btnAssignment3.Text = "JIRA Bugs";
            this.btnAssignment3.TextAlign = System.Drawing.ContentAlignment.BottomCenter;
            this.btnAssignment3.UseVisualStyleBackColor = true;
            this.btnAssignment3.Click += new System.EventHandler(this.btnAssignment3_Click);
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(284, 262);
            this.Controls.Add(this.btnAssignment3);
            this.Controls.Add(this.btnAssignment2);
            this.Name = "Form1";
            this.Text = "Form1";
            this.Load += new System.EventHandler(this.Form1_Load);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Button btnAssignment2;
        private System.Windows.Forms.Button btnAssignment3;
    }
}

