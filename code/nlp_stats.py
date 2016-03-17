#!/usr/bin/env python3

"""
Description
"""


import json
import nltk
import numpy as np
import sys


MIN_TOKEN_LENGTH = 3
_lemmatizer = nltk.WordNetLemmatizer()


class Issue:
    def __init__(self, id, key, title_sentences, desc_sentences):
        self.id = id
        self.key = key
##        print('a', file=sys.stderr)
        title_lemma = [[_lemmatizer.lemmatize(w) for w in s.split()]
                       for s in title_sentences]
        desc_lemma = [[_lemmatizer.lemmatize(w) for w in s.split()]
                      for s in desc_sentences]

        self.title_tag = [nltk.pos_tag(s) for s in title_lemma]
        self.desc_tag = [nltk.pos_tag(s) for s in desc_lemma]
##        print('b', file=sys.stderr)
        
        self.title_sentences = title_sentences
        self.desc_sentences = desc_sentences

    @classmethod
    def from_dict(cls, d):
        return Issue(d['id'], d['key'], d['title'], d['description'])


def add_if_nonzero(val, list_):
    if val != 0:
        list_.append(val)


def update_stats(tagged_sentences, stats_dict):
    if len(tagged_sentences) == 0:
        stats_dict['no_terms'] += 1
        return
    
    stats_dict['sentences'].append(len(tagged_sentences))
    
    nouns = verbs = adjectives = adverbs = terms = 0
    for s in tagged_sentences:
        terms += len(s)
        
        nouns += len([w for w in s if w[1].startswith('NN')])
        verbs += len([w for w in s if w[1].startswith('VB')])
        adjectives += len([w for w in s if w[1].startswith('JJ')])
        adverbs += len([w for w in s if w[1].startswith('RB')])
        
    add_if_nonzero(nouns, stats_dict['nouns'])
    add_if_nonzero(verbs, stats_dict['verbs'])
    add_if_nonzero(adjectives, stats_dict['adjectives'])
    add_if_nonzero(adverbs, stats_dict['adverbs'])
    add_if_nonzero(terms, stats_dict['terms'])


def print_stats(stats, name):
    print(name)
    print('No terms:', stats['no_terms'])
    print('Value\t\tAverage\t\tMedian')
    print('Sentences', np.average(stats['sentences']),
          np.median(stats['sentences']), sep='\t')
    print('Terms', np.average(stats['terms']),
          np.median(stats['terms']), sep='\t\t')
    print('Nouns', np.average(stats['nouns']),
          np.median(stats['nouns']), sep='\t\t')
    print('Verbs', np.average(stats['verbs']),
          np.median(stats['verbs']), sep='\t\t')
    print('Adjectives', np.average(stats['adjectives']),
          np.median(stats['adjectives']), sep='\t')
    print('Adverbs', np.average(stats['adverbs']),
          np.median(stats['adverbs']), sep='\t\t')
    print()


def main():
    count = 0
    titles_counts = {'nouns': [], 'verbs': [], 'adjectives' : [], 'terms': [],
                     'adverbs': [], 'sentences': [], 'no_terms': 0}
    desc_counts = {'nouns': [], 'verbs': [], 'adjectives' : [], 'terms': [],
                   'adverbs': [], 'sentences': [], 'no_terms': 0}
    both_counts = {'nouns': [], 'verbs': [], 'adjectives' : [], 'terms': [],
                   'adverbs': [], 'sentences': [], 'no_terms': 0}
    for line in sys.stdin:
        print('Processing issue', count + 1, file=sys.stderr)
        if count % 10 == 0:
            print('Total number of bugs:', count)
            print_stats(titles_counts, 'Titles')
            print_stats(desc_counts, 'Descriptions')
            print_stats(both_counts, 'Both')
            print()
            
        issue = json.loads(line, object_hook=Issue.from_dict)

        update_stats(issue.title_tag, titles_counts)
        update_stats(issue.desc_tag, desc_counts)
        update_stats(issue.title_tag + issue.desc_tag, both_counts)
        
        count += 1


if __name__ == '__main__':
    main()
