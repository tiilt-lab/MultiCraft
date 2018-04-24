from InitializeDictionaries import materials_dict, directions_dict
from Text2Int import text2int
from TiiltBlocks import get_block_code
from ImportantCoordinates import load_location_dict


class GameCommand:
	def __init__(self):
		self.command = None
		self.args = {}
		self.command_token = ''
		self.command_text = ''
		self.is_valid = False

	def get_game_command_args(self):
		if self.command == 'move':
			return self.get_move_args()
		elif self.command == 'build':
			return self.get_build_args()
		elif self.command == 'save':
			return self.get_save_args()
		elif self.command == 'tilt':
			return self.get_tilt_args()
		elif self.command == 'go':
			return self.get_go_args()
		elif self.command == 'turn':
			return self.get_turn_args()
		else:
			return None

	def get_move_args(self):
		for word_token in self.command_token:
			if word_token.pos_ == u'NUM':
				self.args['dimensions'] = text2int(word_token.text)
			elif word_token.text in directions_dict:
				self.args['direction'] = word_token.text

		if 'dimensions' in self.args.keys():
			if 'direction' not in self.args.keys():
				self.args['direction'] = 'forward'
			self.is_valid = True

	def get_build_args(self):
		dimensions = []
		for word_token in self.command_token:
			if word_token.pos_ == u'NUM':
				dimensions.append(text2int(word_token.text))
			if word_token.text == 'house':
				self.args['house'] = True
			if word_token.text in materials_dict:
				self.args['block_code'] = get_block_code(materials_dict[word_token.text].upper())

		if len(dimensions) < 3:
			self.args = {}
			return
		self.args['dimensions'] = dimensions
		if 'block_code' not in self.args.keys() or self.args['block_code'] is None:
			self.args['block_code'] = get_block_code('STONE')
		self.is_valid = True

	def get_save_args(self):
		command_words = self.command_text.lower().split(' ')
		if len(command_words) >= 2:
			return
		self.args['location_name'] = command_words[1]
		self.is_valid = True

	def get_tilt_args(self):
		for word_token in self.command_token:
			if word_token.pos == u'NUM':
				print 'process dimension'
				self.args['dimensions'] = text2int(word_token.text)
			elif word_token.text in directions_dict:
				self.args['direction'] = word_token.text

		if 'dimensions' in self.args.keys():
			if 'direction' not in self.args.keys():
				self.args['directions'] = 'up'
			self.is_valid = True

	def get_go_args(self):
		locations_dict = load_location_dict()
		for word in self.command_text.split(' '):
			if word in locations_dict.keys():
				self.args['dimensions'] = locations_dict[word]
				self.is_valid = True
				break

	def get_turn_args(self):
		for word_token in self.command_token:
			if word_token.pos_ == u'NUM':
				self.args['dimensions'] = text2int(word_token.text)
			elif word_token.text in directions_dict:
				self.args['direction'] = word_token.text

		if 'direction' in self.args.keys():
			if 'dimensions' not in self.args.keys():
				self.args['dimensions'] = 90
			self.is_valid = True
