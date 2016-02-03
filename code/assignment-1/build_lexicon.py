#!/usr/bin/env python3

"""
Description
"""


import simple_parser

import nltk

import argparse
import inflection
import nltk
import json
import re
import sys
import os


class LexiconBuilder:
    def __init__(self, stop_words, min_length=3):
        self.index = {}
        self.inverted_index = {}
        self.tokenizer = nltk.tokenize.RegexpTokenizer(r"[a-zA-Z0-9']+")
        self.stop_words = stop_words
        self.min_length = min_length
        self.identifier_split_re = re.compile(r'[_$]')
        self.number_re = re.compile(r'^\d+$')
        self.stemmer = nltk.stem.PorterStemmer()
        
    def process_file(self, file_path):
        if file_path in self.index:
            # The file has already been processed
            return

        with open(file_path) as file:
            identifiers, comment_texts = simple_parser.parse(file.read())

        # Tokenize comment texts
        processed_terms = {}
        for text in comment_texts:
            tokens = self.tokenizer.tokenize(text)
            for token in tokens:
                token = self.stemmer.stem(token.lower())
                # Doesn't add stop words or short terms
                self._add_to_dict(token, processed_terms)

        # Split identifiers
        for identifier in identifiers:
            if (len(identifier) < self.min_length or
                identifier in self.stop_words):
                continue
            
            # Add the original identifier
            self._add_to_dict(self.stemmer.stem(identifier.lower()),
                              processed_terms)

            # Converts CamelCase to snake_case and then splits on _ and $
            components = self.identifier_split_re.split(
                inflection.underscore(identifier))
            if len(components) > 1:
                for component in components:
                    component = self.stemmer.stem(component.lower())
                    self._add_to_dict(component, processed_terms)
            
        # TODO: Implement index
        self.index[file_path] = processed_terms

        for (term, count) in processed_terms.items():
            docs = self.inverted_index.get(term, [])
            docs.append((file_path, count))
            self.inverted_index[term] = docs


    def _add_to_dict(self, term, dict_):
        if (len(term) < self.min_length or term in self.stop_words or
            self.number_re.match(term)):
            return

        count = dict_.get(term, 0)
        dict_[term] = count + 1
        

def main(directory, out_dir_path):
    if not out_dir_path:
        out_dir_path = os.getcwd()
    else:
        out_dir_path = out_dir_path.rstrip(os.sep)

    with open('stop_words.txt') as stop_words_file:
        stop_words = set(line.strip('\n') for line in stop_words_file)

    builder = LexiconBuilder(stop_words)

    for (dir_path, _, file_names) in os.walk(directory):
        for file_name in file_names:
            if not file_name.endswith('.java'):
                continue
            file_path = os.path.abspath(os.path.join(dir_path, file_name))
            builder.process_file(file_path)

    with open(os.path.join(out_dir_path, 'index.json'), 'w') as index:
        index.write(json.dumps(builder.index))

    with open(os.path.join(out_dir_path, 'inverted.json'), 'w') as inv:
        inv.write(json.dumps(builder.inverted_index))


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-o', '--out-dir',
                        help=('Directory to write the lexicon to, or cur dir '
                              'if not provided'))
    parser.add_argument('directory',
                        help='A directory where source files will be searched')
    args = parser.parse_args()

    main(args.directory, args.out_dir)
