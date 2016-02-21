#!/usr/bin/env python3

"""
Description
"""


import inflection
import nltk
import ply.lex
import plyj.parser as plyj
import re


def parse(string):
    '''Parses a string of valid java code and returns a tuple
    (identifiers, comment_texts) ignoring import and package statements.'''
    identifiers = []
    comment_texts = []

    lexer = ply.lex.lex(module=plyj.MyLexer(comment_texts, identifiers))
    lexer.input(string)
    for token in lexer:
        # Have to iterate over it to actually lex
        pass

    return (identifiers, comment_texts)


class TextElementTokenizer(object):
    def __init__(self, min_length=3, ignore=None):
        if ignore:
            self.ignore = set(e.lower() for e in ignore)
        else:
            self.ignore = []
        self.min_length = min_length
        self.id_split_re = re.compile(r'[_$]')
        self.tokenizer = nltk.tokenize.RegexpTokenizer(r"[a-zA-Z0-9']+")
        self.stemmer = nltk.stem.PorterStemmer()

    def is_valid_token(self, token):
        return (len(token) >= self.min_length and not token.isnumeric() and
                token.lower() not in self.ignore)
    
    def tokenize_text_elements(self, text_elements):
        """Takes an iterable of text elements of arbitrary length and contents
        and applies tokenization and identifier splitting, also validating
        minimum length and ignored words before and after splitting.
        """
        
        all_tokens = []
        for text_element in text_elements:
            tokens = self.tokenizer.tokenize(text_element)
            for token in tokens:
                if not self.is_valid_token(token):
                    continue

                new_tokens = [token.lower()]

                # Attempt to split the token
                splits = [word for word in
                          self.id_split_re.split(inflection.underscore(token))]
                
                if len(splits) > 1:
                    new_tokens.extend(s for s in splits
                                      if self.is_valid_token(s))
                
                # Finally, stem all new tokens
                all_tokens.extend(self.stemmer.stem(t) for t in new_tokens)

        return all_tokens
    
