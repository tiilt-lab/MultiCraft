from MultiCraftDictionaries import materials_dict, directions_dict
from Text2Int import text2int


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
		elif self.command == 'turn':
			return self.get_turn_args()
		# elif self.command == 'pen':
		# 	pass #return self.get_pen_args()
		elif self.command == 'track':
			return self.get_track_args()
		elif self.command == 'undo':
			return self.get_undo_args()
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
			if word_token.text == 'wall':
				self.args['wall'] = True
			if word_token.text == 'sphere':
				self.args['sphere'] = True
			if word_token.text == 'roof':
				self.args['roof'] = True
			if word_token.text in materials_dict.keys():
				self.args['block_code'] = materials_dict[word_token.text]
			if word_token.text == 'hollow':
				self.args['hollow'] = True

		if 'wall' in self.args.keys() and len(dimensions) == 2:
			dimensions.append(0)
		elif 'sphere' in self.args.keys() and len(dimensions) == 1:
			pass # This is a valid dict therefore skip the return
		elif 'roof' in self.args.keys() and len(dimensions) == 2:
			dimensions.append(0)
		elif len(dimensions) < 3:
			self.args = {}
			return
		self.args['dimensions'] = dimensions
		if 'block_code' not in self.args.keys() or self.args['block_code'] is None:
			self.args['block_code'] = materials_dict["stone"]
		self.is_valid = True

	def get_save_args(self):
		command_words = self.command_text.lower().split(' ')
		if len(command_words) > 2:
			return
		self.args['location_name'] = command_words[1]
		self.is_valid = True

	def get_tilt_args(self):
		for word_token in self.command_token:
			if word_token.pos == u'NUM':
				self.args['dimensions'] = text2int(word_token.text)
			elif word_token.text in directions_dict:
				self.args['direction'] = word_token.text
				if 'direction' in self.args.keys():
					if 'dimension' not in self.args.keys():
						self.args['dimensions'] = 45
					self.is_valid = True

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

	def get_pen_args(self):
		self.args['pen'] = True
		self.is_valid = True

	def get_track_args(self):
		for word_token in self.command_token:
			if word_token.text == 'build':
				self.args['build'] = True
				return self.get_build_args()
			if word_token.text == 'move':
				self.args['move'] = True
		self.is_valid = True

	def get_undo_args(self):
		self.is_valid = True