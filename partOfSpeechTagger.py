import spacy
from text2Int import text2int
from tiiltBlocks import getBlockCode
from nltk.corpus import wordnet as wn


#NEED TO FIX THIS UP. TOO RIGID.
materials = ['stone', 'gold', 'golden', 'brick', 'lava', 'water']
supported_commands = ['move', 'turn', 'build']
directions = ['left', 'right', 'back', 'forward']



'''given an array of commands, for each of the commands synonyms,
this maps the synonym to the command'''

def get_synonyms_dict(commands):
    synonyms_dict = {}

    for word in commands:
        synonyms_dict[word] = word

    for comm in commands:
        comm_synonyms = wn.synsets(comm, pos=wn.VERB)
        for word in comm_synonyms:
            for lemma in word.lemmas():
                if not (lemma.name() in synonyms_dict):
                    synonyms_dict[lemma.name()] = comm
    return synonyms_dict

materialsDisambiguationDict = get_synonyms_dict(materials)
commandsDisambiguationDict = get_synonyms_dict(supported_commands)
directionDisambiguationDict = get_synonyms_dict(directions)

def process_instruction():
    dict1 = {}
    num = []
    command = ""
    user_input = raw_input('Enter a string to process: ')

    doc = nlp(unicode(user_input))
    for token in doc:
        if token.text in supported_commands:
            dict1['command'] = token.text
        elif token.pos_ == u'NUM':
            num.append(text2int(token.text))
        elif token.text.lower() in directions:
            dict1['direction'] = token.text
        elif token.text.lower() in materials:
            dict1['blockCode'] = getBlockCode(token.text.upper())

        if token.text == 'house':
            dict1['house'] = True

    dict1['dimensions'] = num

    return dict1




nlp = spacy.load('en')

while True:
    print(process_instruction())




