import spacy
from GameCommand import GameCommand
from InitializeDictionaries import commands_dict, supported_commands
nlp = spacy.load('en')


def get_supported_word(word, synonyms_dict):
	return synonyms_dict[word]


def process_instruction(instruction_to_process):
	game_command = GameCommand()
	game_command.command_token = nlp(instruction_to_process.lower())
	game_command.command_text = game_command.command_token.text

	for word in game_command.command_text.split(' '):
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
	if game_command.is_valid:
		return game_command.args
	return None


if __name__ == "__main__":
	test_strings = ['pen up']
	for command in test_strings:
			print(command, ': ',	process_instruction(command), '\n')
