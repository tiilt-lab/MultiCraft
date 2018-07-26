# import pyautogui
import code
import argparse
import threading
import os
import socket
import platform
import subprocess
import math
import mcpi.mcpiminecraft as minecraft
from ImportantCoordinates import load_location_dict, add_location_to_database
from coordinate_calculations import *
from MineCraftInterpreter import process_instruction
from mcturtle import *
from SpeechToText import AudioHandler
from Structures import sphere
import sys
from Player import Player
from TranscriptsServer import run_server, TRANSCRIPTS

input_method = ''
eye_tracking = ''
tiiltmod = None
speech_thread = None
commands = {'build', 'move', 'turn', 'save', 'go', 'tilt', 'pen', 'undo'}

try:
	__location__ = os.path.realpath(os.path.join(os.getcwd(), os.path.dirname(__file__)))
	important_locations = load_location_dict(os.path.join(__location__, 'important_locations.txt'), {})
except IOError:
	locations_file = open('important_locations.txt', 'w')
	__location__ = os.path.realpath(os.path.join(os.getcwd(), os.path.dirname(__file__)))
	important_locations = load_location_dict(os.path.join(__location__, 'important_locations.txt'), {})


class TIILTMod(object):
	def __init__(self):
		_commands = [
			self.move,
			self.turn,
			self.build,
			self.place,
			self.save,
			self.go,
			self.tilt,
			self.pen,
			self.undo,
		]
		self.commands = {f.__name__: f for f in _commands}
		self.mc = minecraft.Minecraft.create()
		self.t = Turtle(self.mc)
		self.players = {}
		self.transcripts_thread = None
		self.modvoice_ids = {}

	@classmethod
	def test(cls):
		pass

	def move(self, instruction_dict):
		self.t.penup()
		if instruction_dict['direction'] == 'backward' or instruction_dict['direction'] == 'back':
			self.t.right(180)
		elif instruction_dict['direction'] is 'left':
			self.t.left(90)
		elif instruction_dict['direction'] is 'right':
			self.t.right(90)
		self.t.go(instruction_dict['dimensions'])
		return 'executed'

	def go(self, instruction_dict):
		self.t.penup()
		if 'location_name' in instruction_dict.keys() and instruction_dict['location_name'] is not None:
			coordinates = important_locations[instruction_dict['location_name']]
			self.t.goto(coordinates[0], coordinates[1], coordinates[2])
		else:
			dimensions = instruction_dict['dimensions']
			try:
				self.t.goto(dimensions[0], dimensions[1], dimensions[2])
			except IndexError:
				return 'Please specify the location to move to'

	def place(self, instruction_dict):
		pass

	def build(self, instruction_dict):
		# self.t.gridalign()
		if 'sphere' in instruction_dict.keys():
			sphere(instruction_dict['dimensions'][0], instruction_dict['block_code'])
			return 'executed'

		dimensions = instruction_dict['dimensions']
		pos = self.mc.entity.getPos(instruction_dict['player_id'])
		rotation = self.mc.entity.getRotation(instruction_dict['player_id'])
		start_x, start_y, start_z, end_x, end_y, end_z = get_build_coordinates(pos, rotation, dimensions)
		self.mc.setBlocks(start_x, start_y, start_z, end_x, end_y, end_z, instruction_dict['block_code'])
		if 'house' in instruction_dict.keys() and instruction_dict["house"]:
			start_x, start_y, start_z, end_x, end_y, end_z = get_hollow_dimensions(
				start_x, start_y, start_z, end_x, end_y, end_z, rotation, dimensions
			)
			self.mc.setBlocks(start_x, start_y, start_z, end_x, end_y, end_z, 0)
		return 'executed'

	def turn(self, instruction_dict):
		if instruction_dict['direction'] == 'backward' or instruction_dict['direction'] == 'back':
			amount = instruction_dict['dimensions']
			self.t.right(amount)
		elif instruction_dict['direction'] == 'left':
			amount = instruction_dict['dimensions']
			self.t.left(amount)
		elif instruction_dict['direction'] == 'right':
			amount = instruction_dict['dimensions']
			self.t.right(amount)
		return 'executed'

	def tilt(self, instruction_dict):
		if instruction_dict['direction'] == 'up':
			self.t.up(instruction_dict['dimensions'])
		elif instruction_dict['direction'] == 'down':
			self.t.down(instruction_dict['dimensions'])
		return 'executed'

	def save(self, instruction_dict):
		coordinates = [int(self.mc.player.getPos().x), int(self.mc.player.getPos().y), int(self.mc.player.getPos().z)]
		important_locations[instruction_dict['location_name']] = coordinates
		add_location_to_database(
			instruction_dict['location_name'],coordinates,
			os.path.join(__location__, 'important_locations.txt'))
		return 'executed'

	def pen(self):
		self.t.penup()
		return 'executed'

	def undo(self):
		self.mc.postToChat("Undoing action")
		self.mc.restoreCheckpoint()

	def orient_player_to_grid(self):
		self.t.goto(self.mc.player.getPos().x, self.mc.player.getPos().y, self.mc.player.getPos().z)
		self.t.angle(self.mc.player.getRotation())

	def protrude_eye_gaze(self):
		player_loc = self.mc.player.getPos()
		player_loc.y += 1
		self.mc.postToChat(player_loc)
		for i in range(0, 10):
			x2 = player_loc.x + (i * math.sin(math.radians(self.mc.player.getRotation())) * math.cos(math.radians(-self.mc.player.getPitch())))
			y2 = player_loc.y + (i * math.sin(math.radians(-self.mc.player.getPitch())))
			z2 = player_loc.z + (i * math.cos(math.radians(-self.mc.player.getPitch())) * math.cos(math.radians(self.mc.player.getRotation())))
			player_loc.x += 1
			block_id = self.mc.getBlock(x2, y2, z2)
			if block_id > 0:
				self.mc.postToChat("Inserting Block")
				self.mc.postToChat(i)
				self.mc.postToChat(str(x2) + ', ' + str(y2) + ', ' + str(z2))
				self.mc.setBlock(x2, y2, z2, 23)
				break
			if i == 9:
				self.mc.postToChat("No block was reached")
		self.mc.postToChat('Your angle orientation is: ' + str(self.mc.player.getRotation()))
		self.mc.postToChat('Your pitch orientation is: ' + str(self.mc.player.getPitch()))
		return "Has been executed"

	def eye_data_setup(self):
		if platform.system() == 'Windows':
			self.mc.postToChat('Starting eye gaze tracking...')
			try:
				eye_gaze = subprocess.Popen("eye_gaze\Interaction_Streams_101.exe", shell=False)
			except Exception as e:
				raise e

		sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

		# Bind the socket to the port
		server_address = ('localhost', 8080)
		self.mc.postToChat('starting up on {} port {}'.format(*server_address))
		sock.bind(server_address)

		# Listen for incoming connections
		sock.listen(1)

		while True:
			self.mc.postToChat('waiting for a connection')
			connection, client_address = sock.accept()
			try:
				# self.mc.postToChat('connection from', client_address)
				# this variable is for debugging purposes
				print_count = 0
				while True:
					print_count += 1
					data = connection.recv(50)
					data = data.decode("utf-8").replace(" ", "")
					coordinates = data.split(":")
					if len(coordinates) == 2:
						try:
							x_coord = int(float(coordinates[0]))
							y_coord = int(float(coordinates[1]))
							if print_count % 10 == 0:
								self.mc.postToChat("You are looking at %s , %s" % (x_coord, y_coord))
								# pyautogui.moveTo(x_coord, y_coord)
						except ValueError:
							continue
					if data:
						continue
					else:
						self.mc.postToChat('No data from', client_address)
						break

			finally:
				connection.close()

	def check_and_execute_commands(self):
		for key, value in self.players.items():
			if len(value.audio_handler.TRANSCRIPT_RESULTS_QUEUE) > 0:
				input_message = value.audio_handler.TRANSCRIPT_RESULTS_QUEUE.popleft()
				self.mc.postToChat(value.name)
				self.mc.postToChat(input_message)
				response = self.execute_instruction(input_message, value.id)

	def execute_instruction(self, instruction, player_id):
		self.mc.postToChat("Executing command")
		instruction_dict = process_instruction(instruction)
		if instruction_dict is None:
			return 'The command was not recognized'
		elif instruction_dict['command'] in self.commands:
			self.mc.postToChat(instruction_dict)
			instruction_dict['player_id'] = player_id
			func = instruction_dict['command']
			kwargs = instruction_dict
			self.commands[func](kwargs)

	def input_line(self, prompt):
		global input_method
		self.mc.events.clearAll()
		while True:
			response = None
			self.update_modvoice_ids()
			if input_method == 'voice':
				while len(TRANSCRIPTS) > 0:
					command = TRANSCRIPTS.popleft()
					self.mc.postToChat(command)
					command_words = command.split()
					if command_words[0] not in self.players.keys():
						self.mc.postToChat("Registering new player")
						new_player = Player()
						new_player.id = self.modvoice_ids[command_words[0].lower()]
						self.mc.postToChat("Associated player with ID")
						new_player.name = command_words[0]
						self.players[new_player.name] = new_player
						self.mc.postToChat(command_words[0] + " has been registered.")
					elif command_words[0] in self.players.keys():
						self.mc.postToChat("Processing command")
						response = self.execute_instruction(command.split(' ', 1)[1], self.players[command_words[0]].id)
						self.mc.postToChat(response)
					else:
						self.mc.postToChat(command_words[0] + " has not completed registration.")
			else:
				chats = self.mc.events.pollChatPosts()
				for c in chats:
					if c.message == 'quit':
						return 'quit()'
					elif c.message == ' ':
						return ''
					elif "__" in c.message:
						sys.exit()
					else:
						response = self.execute_instruction(c.message, c.entityId)
			if response == 'executed':
				pass
			elif response is not None:
				pass
			time.sleep(2)

	def update_modvoice_ids(self):
		chats = self.mc.events.pollChatPosts()
		for c in chats:
			if len(c.message.split()) == 2 and c.message.split()[0] == 'register':
				self.mc.postToChat("Found a new player id to register")
				self.modvoice_ids[c.message.split()[1].lower()] = c.entityId


def mod():
	"_mcp"
	global tiiltmod
	tiiltmod = TIILTMod()
	tiiltmod.mc.postToChat("Enter python method chat, type 'quit' to quit.")
	i = code.interact(banner="", readfunc=tiiltmod.input_line, local=locals())


def modvoice():
	"_mcp_"
	global tiiltmod, input_method
	input_method = "voice"
	tiiltmod = TIILTMod()
	tiiltmod.transcripts_thread = threading.Thread(
		target=run_server, daemon=True)
	tiiltmod.transcripts_thread.start()
	tiiltmod.mc.postToChat("Speak into the microphone to execute commands")
	i = code.interact(banner="", readfunc=tiiltmod.input_line, local=locals())


def close_stuff():
	"_mcp_"
	global tiiltmod
	del tiiltmod.transcripts_thread
	del tiiltmod
	del TRANSCRIPTS
