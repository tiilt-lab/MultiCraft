import en_core_web_sm # Windows
# import spacy # macOS

from GameCommand import GameCommand
from SynonymDictionaries import commands_dict, supported_commands
import json
import socket

nlp = en_core_web_sm.load() # windows
#nlp = spacy.load("en_core_web_sm") # macOS
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
		else: 
			return None

	if game_command.command is None:
		game_command.is_valid = False
		return None

	game_command.args['command'] = game_command.command
	game_command.get_game_command_args()
	if game_command.is_valid:
        # TODO : Create JSON File to Send to Java
		return game_command.args
	return None


def create_json(game_command):
	command_json = {}
	command_json['name'] = game_command.command
	command_json['args'] = json.dumps(game_command.args)
	return command_json


if __name__ == "__main__":
	while True:
		input_s = input("Please enter a message to interprete: ")
		print(process_instruction(input_s))
