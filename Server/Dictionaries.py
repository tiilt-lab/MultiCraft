import json

from nltk.corpus import wordnet as wn


def get_word_synonyms_as_dict(commands):
    """
    Given a list of strings, map every synonym of a given string to the string itself

    Parameters
    ----------
    commands : list
        List of strings representing the command

    Returns
    -------
    synonyms_dict : dict
        Dictionary mapping each word in the original list to its synonyms
    """
    synonyms_dict = {}

    for word in commands:
        synonyms_dict[word] = word

    for comm in commands:
        for word in get_word_synonyms(comm):
            synonyms_dict[word] = comm
    return synonyms_dict


def get_word_synonyms(word):
    """
    Given a word, return a list of its synonyms

    Parameters
    ----------
    word : str
        Word for which to find synonyms for
    
    Returns
    -------
    synonyms : list
        List of synonyms for word found by 
    """
    synonyms = []
    command_synonyms = wn.synsets(word)
    for word in command_synonyms:
        synonyms.extend([lemma.name() for lemma in word.lemmas() if lemma.name() not in synonyms])

    return synonyms


# List of commands supported by the system
supported_commands = ['build', 'place', 'move', 'turn', 'tilt', 'undo', 'redo', 'store', 'clone', 'give']
# supported_commands.append('track')

# List of the movement direction supported
supported_directions = ['up', 'down', 'left', 'right', 'forward', 'back']

# Required dictionaries for command interpretation
directions_dict = get_word_synonyms_as_dict(supported_directions)

f = open('./materials.json')
materials_dict = json.load(f)
f.close()