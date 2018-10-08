tiiltBlocks = {
	'GRASS': 2, 'STONE': 1, 'AIR': 0, 'GOLD': 41, 'WATER': 9, 'LAVA': 10, 'BRICK': 45, 'FIRE': 51,
	'STAIR': 135, 'DOOR': 428, 'TNT': 46}


def get_block_code(block_name):
	if block_name in tiiltBlocks.keys():
		return tiiltBlocks[block_name]
	return None