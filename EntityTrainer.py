import spacy
import logging



logging.debug('Loading Dictionary...')
nlp = spacy.load('en', add_vectors = False)
logging.debug('Dictionary Loaded')

matcher = spacy.matcher.Matcher(nlp.vocab)


def merge_phrases(matcher, doc, i, matches):
    """
    Merge a phrase. We have to be careful here because we'll change the token indices.
    To avoid problems, merge all the phrases once we're called on the last match.
    """
    if i != len(matches) - 1:
        return None
    spans = [(ent_id, label, doc[start: end]) for ent_id, label, start, end in matches]
    for ent_id, label, span in spans:
        span.merge(u'NNP' if label else span.root.tag_, span.text, nlp.vocab.strings[label])


def train(filename, label):
    with open(filename) as f:
        content = f.readlines()
    # you may also want to remove whitespace characters like `\n` at the end of each line
    content = [x.strip() for x in content]

    key = 1
    for line in content:
        matcher.add(entity_key=str(key), label=label, attrs={}, specs=[[{spacy.attrs.ORTH: line.decode('utf-8')}]], on_match=merge_phrases)
        key += 1


# Possible addition...
# Have it fuzzy search the plain text across all entity names
# If it's very similar, either create a new entity with the new name or 'autocorrect' to the existing entity name

def match(doc):
    matcher(doc)
    ets = []
    for ent in doc.ents:
        ets.append(ent)
    return ets
