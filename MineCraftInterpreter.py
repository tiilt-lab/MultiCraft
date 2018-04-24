import spacy
from GameCommand import GameCommand
from InitializeDictionaries import commands_dict, supported_commands
nlp = spacy.load('en')

def get_supported_word(word, synonyms_dict):
	return synonyms_dict[word]

def process_instruction(instruction_to_process):
	instruction_token = nlp(unicode(instruction_to_process).lower())
	instruction_text = instruction_token.text
	game_command = GameCommand()
	game_command.command_token = instruction_token
	game_command.command_text = instruction_text
	for word in instruction_text.split(' '):
		if word in supported_commands:
			game_command.command = word
			break
		elif word in commands_dict.keys():
			game_command.command = get_supported_word(word, commands_dict)
			break
	if game_command.command is None:
		return None
	game_command.args['command'] = game_command.command
	game_command.get_game_command_args()
	print game_command.args
	if game_command.is_valid:
		return game_command.args
	return None

# test_strings = ['move forward sixteen steps',
# 				'tilt body seventy degrees down',
# 				'turn forty degrees right',
# 				'build a thing measuring ten by ten by five',
# 				'build a house measuring ten by six',
# 				'build a square measuring four by four by one',
# 				'save home2',
# 				'go to home2'
# 				]
#
# for command in test_strings:
# 	print command, ': ',	process_instruction(command), '\n'