#!/usr/bin/env python3

"""
Description
"""


import argparse
import sys
import json


def main():
    queries = [json.loads(line) for line in open(sys.argv[1])]
    results = [json.loads(line) for line in open(sys.argv[2])]

    for query, result in zip(queries, results):
        gold_set = set(query['doc_ids'])

        for t in ['title_only', 'description_only', 'title_and_description']:
##            print(t)
            docs = result[t]
            eff = None
            for cut in [5, 10, 20]:
                slice_ = docs[:cut]
                relevant = [e for e in slice_ if e in gold_set]
                if eff is None and relevant:
                    eff = slice_.index(relevant[0]) + 1
                    
                recall = len(relevant) / len(gold_set)
                precision = len(relevant) / cut
##                print('at',cut)
                print(precision, end=';')
                print(recall, end=';')
            print(eff, end=';')
        print()


if __name__ == '__main__':
##    parser = argparse.ArgumentParser()
##    parser.add_argument('number', help='An int value', type=int)
##    parser.add_argument('-v', '--verbose', help='Makes it verbose',
##                        action='store_true')
##    parser.add_argument('-a', '--amount', help='amount', type=int)

##    args = parser.parse_args()
##    print(args.number)
##    if args.verbose:
##        pass

    main()
