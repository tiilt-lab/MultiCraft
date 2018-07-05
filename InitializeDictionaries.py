from nltk.corpus import wordnet as wn

# List of the building materials supported
materials = ['stone', 'gold', 'golden', 'brick', 'lava', 'water']

# List of the commands supported
supported_commands = ['move', 'turn', 'build', 'save', 'go', 'tilt', 'pen', 'undo']

# List of the directions for movement supported
directions = ['left', 'right', 'back', 'forward', 'up', 'down']


# Given a list of strings, this maps every synonym of
# a given string to the string itself
def get_word_synonyms_as_dict(commands):
	synonyms_dict = {}

	for word in commands:
		synonyms_dict[word] = word

	for comm in commands:
		for word in get_word_synonyms(comm):
			synonyms_dict[word] = comm
	return synonyms_dict


def get_word_synonyms(word):
	synonyms = []
	comm_synonyms = wn.synsets(word, pos=wn.VERB)
	for word in comm_synonyms:
		for lemma in word.lemmas():
			if not (lemma.name() in synonyms):
				synonyms.append(lemma.name())
	return synonyms


# Required Dictionaries

materials_dict = get_word_synonyms_as_dict(materials)
commands_dict = get_word_synonyms_as_dict(supported_commands)
directions_dict = get_word_synonyms_as_dict(directions)


