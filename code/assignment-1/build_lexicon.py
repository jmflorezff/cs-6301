#!/usr/bin/env python3

"""
Description
"""


import simple_parser

import nltk

import argparse
import sys


class LexiconBuilder:
    def __init__(self, stop_words, min_length=3):
        self.index = {}
        self.tokenizer = nltk.tokenize.RegexpTokenizer(r"[a-zA-Z0-9']")
        self.stop_words = stop_words
        self.min_length = min_length
        
    def process_file(file_path):
        if file_path in self.index:
            # The file has already been processed
            return

        with open(file_path) as file:
            identifiers, comment_texts = simple_parser.parse(file.read())

            # Tokenize comment texts
            comment_tokens = set()
            for text in comment_texts:
                comment_tokens.update(self.tokenizer.tokenize(text))

            # Split identifiers
            split_identifiers = set()
            for identifier in identifiers:
                # Add the original identifier
                split_identifiers.add(identifier)
                
            # TODO: Implement stemming
            

def main(directories, outfile):
    if not outfile:
        outfile = sys.stdout
    else:
        outfile = open(outfile, 'w')

    with open('stop_words.txt') as stop_words_file:
        stop_words = set(line.strip('\n') for line in stop_words_file)

    for directory in directories:
        for (dir_path, _, file_names) in os.walk(directory):
            for file_name in file_names:
                file_path = os.path.abspath(os.path.join(dir_path, file_name))
                process_file(file_path)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-o', '--out-file',
                        help=('File to write the lexicon to, or stdout '
                              'if not provided'))
    parser.add_argument('directories',
                        metavar='directory',
                        help='A directory where source files will be searched',
                        nargs='+')
    args = parser.parse_args()

    main(args.directories, args.out_file)
