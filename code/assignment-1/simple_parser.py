#!/usr/bin/env python3

"""
Description
"""


import ply.lex
import plyj.parser as plyj


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
