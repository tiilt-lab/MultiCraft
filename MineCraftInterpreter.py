import en_core_web_sm
from Text2Int import text2int
from TiiltBlocks import getBlockCode
from nltk.corpus import wordnet as wn

nlp = en_core_web_sm.load()

# List of the building materials supported
materials = ['stone', 'gold', 'golden', 'brick', 'lava', 'water']

# List of the commands supported
supported_commands = ['move', 'turn', 'build', 'save', 'go', 'tilt']

# List of the directions for movement supported

directions = ['left', 'right', 'back', 'forward', 'up', 'down']


'''
	Given a list of strings, this maps every synonym of 
	a given string to the string itself.
'''


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


'''
Create the dictionaries for materials, commands and directions
to help with word sense disambiguation
'''


materials_dict = get_synonyms_dict(materials)
commands_dict = get_synonyms_dict(supported_commands)
directions_dict = get_synonyms_dict(directions)


'''
	Takes an instruction represented as a String and return a dictionary
	with the required arguments mapped to their values
'''


def process_instruction(instruction_to_process):

	dict1 = {
		'command': None,
		'house': None,
		'material': None,
		'dimensions': [],
		'blockCode': None,
		'direction': None,
	}

	num = []

	doc = nlp(unicode(instruction_to_process).lower())
	for token in doc:
		if token.text in commands_dict:
			dict1['command'] = commands_dict[token.text]
			if token.text == 'save':
				dict1['location_name'] = instruction_to_process.lower().split(' ')[1]
			elif token.text == 'go':
				words = instruction_to_process.lower().split(' ')
				dict1['location_name'] = words[len(words)-1]
		elif token.pos_ == u'NUM':
			num.append(text2int(token.text))
		elif token.text in directions_dict:
			dict1['direction'] = directions_dict[token.text]
		elif token.text in materials_dict:
			dict1['blockCode'] = getBlockCode(materials_dict[token.text].upper())
		elif token.text == 'house':
			dict1['house'] = True

	if len(num) != 0:
		dict1['dimensions'] = num

	if dict1['command'] == 'build' and (not ('material' in list(dict1.keys()))):
		dict1['material'] = getBlockCode('STONE')

	return dict1