#!/usr/bin/env python3

"""
Description
"""

import json
import csv

weka = json.loads(
    open('/home/juan/Source/cs-6301/results/assignment-1/weka_inverted.json').read())
free = json.loads(
    open('/home/juan/Source/cs-6301/results/assignment-1/freemind_inverted.json').read())

weka_tf ={term: sum(count for _, count in weka[term]) for term in weka}
##weka_df = {term: len(weka[term]) for term in weka}
##
free_tf ={term: sum(count for _, count in free[term]) for term in free}
##free_df = {term: len(free[term]) for term in free}

with open('/home/juan/Source/cs-6301/results/assignment-1/weka.csv', 'w') as ow:
    w = csv.writer(ow)
    w.writerow(('term', 'count'))
    for e in sorted(weka_tf.items(), key=lambda x: x[1], reverse=True):
        w.writerow(e)

with open('/home/juan/Source/cs-6301/results/assignment-1/free.csv', 'w') as ow:
    w = csv.writer(ow)
    w.writerow(('term', 'count'))
    for e in sorted(free_tf.items(), key=lambda x: x[1], reverse=True):
        w.writerow(e)

##print('weka top tf', *sorted(weka_tf.items(), key=lambda x: x[1], reverse=True)[:10], sep='\n')
##print('free top tf', *sorted(free_tf.items(), key=lambda x: x[1], reverse=True)[:10], sep='\n')
##
##print('weka top df', *sorted(weka_df.items(), key=lambda x: x[1], reverse=True)[:10], sep='\n')
##print('free top df', *sorted(free_df.items(), key=lambda x: x[1], reverse=True)[:10], sep='\n')

##weka_ = json.loads(
##    open('/home/juan/Source/cs-6301/results/assignment-1/weka_index.json').read())
##free_ = json.loads(
##    open('/home/juan/Source/cs-6301/results/assignment-1/freemind_index.json').read())
##
##weka_doc = [(doc, sum(a[1] for a in terms.items())) for doc, terms in weka_.items()]
##free_doc = [(doc, sum(a[1] for a in terms.items())) for doc, terms in free_.items()]
##
##print('most uniq weka', *sorted(weka_doc, key=lambda x: x[1], reverse=True)[:3], sep='\n')
##print('less uniq weka', *sorted(weka_doc, key=lambda x: x[1])[:3], sep='\n')
##
##print('most uniq free', *sorted(free_doc, key=lambda x: x[1], reverse=True)[:3], sep='\n')
##print('less uniq free', *sorted(free_doc, key=lambda x: x[1])[:3], sep='\n')
