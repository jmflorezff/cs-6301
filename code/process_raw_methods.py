#!/usr/bin/env python3

"""
Description
"""


import argparse
import os
import json
import simple_parser
import sys


def main():
    dirname = os.path.dirname(__file__)

    tokenizer = simple_parser.TextElementTokenizer(2,
        ([line.strip('\n')
          for line in open(os.path.join(dirname, 'java_api_classes.txt'))] +
         [line.strip('\n')
          for line in open(os.path.join(dirname, 'stop_words.txt'))] +
         [line.strip('\n')
          for line in open(os.path.join(dirname, 'java_keywords.txt'))]))
    
    for line in sys.stdin:
        raw_method = json.loads(line)
        all_tokens = tokenizer.tokenize_text_elements(
            raw_method['text_elements'] + raw_method['inner_comments'])

        print(json.dumps({
            'id': raw_method['id'],
            'file_name': raw_method['file_name'][18:],
            'name': raw_method['name'],
            'tokens': all_tokens}))


if __name__ == '__main__':
##    parser = argparse.ArgumentParser()
##    parser.add_argument('number', help='An int value', type=int)
##    parser.add_argument('-v', '--verbose', help='Makes it verbose',
##                        action='store_true')b
##    parser.add_argument('-a', '--amount', help='amount', type=int)

##    args = parser.parse_args()
##    print(args.number)
##    if args.verbose:
##        pass

    main()
##    import os
##    print(os.path.realpath(__file__))
##    print(os.getcwd())
