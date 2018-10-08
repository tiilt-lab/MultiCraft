import spacy
from GameCommand import GameCommand
from SynonymDictionaries import commands_dict, supported_commands
import json
import socket

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
		game_command.is_valid = False
		return game_command

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
	gcomm = GameCommand()
	gcomm.command = "move"
	gcomm.args["direction"] = "left"
	gcomm.args["dimensions"] = [4,5,6]
	print(create_json(gcomm))
	client_socket = socket.socket()
	client_socket.connect(("127.0.0.1", 5000))
	client_socket.send(json.dumps(create_json(gcomm)).encode())
	print("Message has been sent")
	client_socket.close()
	