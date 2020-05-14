from Dictionaries import supported_commands, materials_dict, directions_dict

from word2number import w2n


class GameCommand:
	def __init__(self):
		self.is_valid = False
		self.command = None
		self.command_token = None
		self.command_text = ''
		self.args = {}
		self.arg_methods = {
			'build': self.get_build_args, # mmbuild
			'place': self.get_place_args, 
			'move': self.get_move_args,
			'track': self.get_track_args, # eyebuild/eyetrack
			'turn': self.get_look_args,
			'tilt': self.get_look_args,
			'store': self.get_storage_args, # mstore
			'clone': self.get_storage_args, # mclone,
			'give': self.get_give_args
		}
		

	def get_game_command_args(self):
		comm = self.command
		if comm and comm in supported_commands:
			if (comm != 'undo' and comm != 'redo'):
				self.arg_methods[comm]()
			else: self.is_valid = True


	def get_build_args(self):
		build_shapes = ['wall', 'roof', 'house', 'sphere']
		build_shape_selected = False
	
		# Parse for build_shape and hollow tags
		for word_token in self.command_token:
			word = word_token.text
			if not build_shape_selected and word in build_shapes:
				build_shape_selected = True
				self.args[word] = True
			elif word == 'hollow':
				self.args['hollow'] = True

		# Validate configurations of build_shape and dimensions
		dimensions = self.get_dimensions()
		if ('wall' in self.args.keys() or 'roof' in self.args.keys()) and len(dimensions) == 2:
			dimensions.append(0)
		elif len(dimensions) < 3 and not ('sphere' in self.args.keys() and len(dimensions) == 1):
			return
		
		# Set dimensions and material
		self.args['dimensions'] = dimensions
		self.set_material()
		
		# Add build flag if this is a track command
		if self.command == 'track': self.args['build'] = True
	
		self.is_valid = True


	def get_place_args(self):
		self.set_material()
		self.is_valid = True


	def get_move_args(self):
		# Get movement dimensions
		dimensions = self.get_dimensions()
		if not len(dimensions): return
		self.args['dimensions'] = dimensions[0]

		# Set movement directions, if direction was not defined, default to 'forward'
		self.set_direction()
		if 'direction' not in self.args.keys(): self.args['direction'] = 'forward'
		
		self.is_valid = True


	def get_track_args(self):
		# Search for build or move keywords
		for word_token in self.command_token:
			if word_token.text == 'build':
				self.get_build_args()
			elif word_token.text == 'move':
				self.args['move'] = True

		self.is_valid = True


	def get_look_args(self):
		# Set the turn/tilt dimension
		default_degrees = 90 if self.command == 'turn' else 45
		dimensions = self.get_dimensions()
		self.args['dimensions'] = dimensions[0] if len(dimensions) else default_degrees

		# Set turn/tilt direction, then check if direction was defined
		self.set_direction()
		if 'direction' in self.args.keys(): self.is_valid = True


	def get_storage_args(self):
		if self.command == 'clone' and len(self.command_token) > 1:
			self.args['name'] =  self.command_token[1].text
			self.is_valid = True
		# somehow extract a name out of this instead of defaulting to the last word
		elif self.command == 'store' and len(self.command_token) > 1:
			self.args['name'] = self.command_token[-1].text
			self.is_valid = True


	def get_give_args(self):
		# Give the user
		if len(self.command_token) > 1:
			self.set_material()
			self.args['dimensions'] = dimensions[0] if len(dimensions) else 1
			self.is_valid = True


	def set_material(self):
		# Set the first material word found in the token as the block_code argument
		for word_token in self.command_token:
			if word_token.text in materials_dict.keys():
				self.args['block_code'] = materials_dict[word_token.text]
				break

		# Default to stone if no material word was found	
		if 'block_code' not in self.args.keys() or self.args['block_code'] is None:
			self.args['block_code'] = materials_dict['stone']

	
	def set_direction(self):
		# Set the first direction word found in the token as the direction argument
		for word_token in self.command_token:
			if word_token.text in directions_dict:
				self.args['direction'] = word_token.text
				break
		

	def get_dimensions(self):
		"""
		Parse the current command token and return an array of dimensions (ints).

		Parameters
		----------
		None

		Returns
		-------
		dimensions : list
			List of parsed numbers (ints) from the tokenized command in the order
			that they appear.
		"""

		defining_number = False
		current_number = ''
		dimensions = []

		for word_token in self.command_token:
			if word_token.pos_ == 'NUM':
				defining_number = True
				current_number += word_token.text + ' '
			else:
				if defining_number: dimensions.append(w2n.word_to_num(current_number.strip()))
				
				defining_number = False
				current_number = ''

		if defining_number: 
			dimensions.append(w2n.word_to_num(current_number.strip()))

		return dimensions

		