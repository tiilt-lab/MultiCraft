from GameCommand import GameCommand
from Dictionaries import supported_commands

import spacy


# Initialize NLP
NLP = spacy.load("en_core_web_sm")


def get_supported_word(word, synonyms_dict):
	return synonyms_dict[word]


def process_instruction(instruction_to_process):
	game_command = GameCommand()
	game_command.command_token = NLP(instruction_to_process.lower())
	game_command.command_text = game_command.command_token.text

	for word in game_command.command_text.split(' '):
		if word in supported_commands:
			game_command.command = word
			break

	if game_command.command is None: return

	game_command.args['command'] = game_command.command
	game_command.get_game_command_args()

	if game_command.is_valid:
		return game_command.args
	

# For testing interpretation capabilities, you can run this file directly
if __name__ == "__main__":
	while True:
		input_s = input("Please enter a message to interpret: ")
		print(process_instruction(input_s))
