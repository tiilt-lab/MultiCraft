from mcturtle import *
import code
import time
import sys
import SpeechToText as Spt
from MineCraftInterpreter import process_instruction
import argparse
from ImportantCoordinates import load_location_dict, add_location_to_database
import os
from coordinate_calculations import *

sys.path.append('.')

# Get the specified input method
parser = argparse.ArgumentParser(description='Specifies options (Voice Input or Text Input) for command input.')
parser.add_argument('--input', dest='input_method', default='text', help='Specify the input method that will be used \
 (default: text)', choices=['voice', 'text'])
args = vars(parser.parse_args())
input_method = args['input_method']

commands = {'build', 'move', 'turn', 'save', 'go', 'tilt', 'pen'}

try:
	sd = Spt.SpeechDetector()
	sd.setup_mic()
except IOError:
	pass

# Load my saved locations
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
		]
		# mc = minecraft.Minecraft()
		self.commands = {f.__name__: f for f in _commands}
		self.mc = minecraft.Minecraft()
		self.playerPos = self.mc.player.getPos()
		self.playerId = self.mc.getPlayerId()
		self.t = Turtle(self.mc)

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
		# if len(instruction_dict['dimensions']) == 0:
		# 	return 'Please specify the number of steps the player should move'
		self.t.go(instruction_dict['dimensions'])
		return 'executed'

	def go(self, instruction_dict):
		self.t.penup()
		if instruction_dict['location_name'] is not None and instruction_dict['location_name'] in instruction_dict.keys():
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
		self.t.gridalign()
		comms = instruction_dict['dimensions']
		pos = self.mc.player.getPos()
		rotation = self.mc.player.getRotation()
		start_x, start_y, start_z, end_x, end_y, end_z = get_build_coordinates(pos, rotation, comms)
		self.mc.postToChat(str(end_x) + ' ' + str(end_y) + ' ' + str(end_z))
		block_code = instruction_dict['block_code']
		self.mc.setBlocks(start_x, start_y, start_z, end_x, end_y, end_z, block_code)
		if 'house' not in instruction_dict.keys():
			return 'executed'
		elif instruction_dict['house'] is True:
			start_x += 1
			start_y += 1
			start_z += 1
			end_x -= 2
			end_y -= 2
			end_z -= 1
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
			self.mc.postToChat('in here here here')
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
		add_location_to_database(instruction_dict['location_name'],coordinates,
								 os.path.join(__location__, 'important_locations.txt'))
		return 'executed'

	def pen(self, instruction_dict):
			self.t.penup()
			return 'executed'
	def execute_instruction(self, instruction):
		instruction_dict = process_instruction(instruction)
		if instruction_dict is None:
			return 'The command was not recognized'
		elif instruction_dict['command'] in self.commands:
			self.mc.postToChat(instruction_dict)
			# orient player to grid
			self.orient_player_to_grid()
			func = instruction_dict['command']
			kwargs = instruction_dict
			self.commands[func](kwargs)
		self.mc.postToChat('Your angle orientation is: ' + str(self.mc.player.getRotation()))
		self.mc.postToChat('Your position on the map is: ' + str(self.mc.player.getPos()))

	def orient_player_to_grid(self):
		self.t.goto(self.mc.player.getPos().x, self.mc.player.getPos().y, self.mc.player.getPos().z)
		self.t.angle(self.mc.player.getRotation())

	def input_line(self, prompt):
		self.mc.events.clearAll()
		while True:
			response = None
			if input_method == 'voice':
				self.mc.postToChat('running via mic')
				input_message = sd.run()
				self.mc.postToChat(input_message)
				response = self.execute_instruction(input_message)
			else:
				chats = self.mc.events.pollChatPosts()
				for c in chats:
					if c.entityId == self.playerId:
						if c.message == 'quit':
							return 'quit()'
						elif c.message == ' ':
							return ''
						elif "__" in c.message:
							sys.exit()
						else:
							self.mc.postToChat(c.message)
							response = self.execute_instruction(c.message)
			if response == 'executed':
				self.mc.postToChat('executed')
				pass
			elif response is not None:
				self.mc.postToChat(response)
			time.sleep(0.2)

if __name__ == '__main__':
	player = TIILTMod()
	player.mc.postToChat("Enter python code into chat, type 'quit' to quit.")
	i = code.interact(banner="", readfunc=player.input_line, local=locals())
