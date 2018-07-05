def get_build_coordinates(player_pos, player_angle, structure_dimensions):
	start_x = player_pos.x
	start_y = player_pos.y
	start_z = player_pos.z

	if -45 <= player_angle <= 45:
		print('here')
		start_x -= 1
		end_x, end_y, end_z = get_facing_pos_z_coordinates(start_x, start_y, start_z, structure_dimensions)
	elif 45 < player_angle <= 135:
		print('here 1')
		start_x -= 1
		temp_var = structure_dimensions[2]
		structure_dimensions[2] = structure_dimensions[0]
		structure_dimensions[0] = temp_var
		end_x, end_y, end_z = get_facing_neg_x_coordinates(start_x, start_y, start_z, structure_dimensions)
	elif 135 < player_angle or player_angle <= -135:
		print('here 2')
		start_z -= 1
		end_x, end_y, end_z = get_facing_neg_z_coordinates(start_x, start_y, start_z, structure_dimensions)
	else:
		print('here 3')
		temp_var = structure_dimensions[2]
		structure_dimensions[2] = structure_dimensions[0]
		structure_dimensions[0] = temp_var
		end_x, end_y, end_z = get_facing_pos_x_coordinates(start_x, start_y, start_z, structure_dimensions)
	
	return start_x, start_y, start_z, end_x, end_y, end_z


def get_facing_pos_z_coordinates(start_x, start_y, start_z, structure_dimensions):
	end_z = start_z + structure_dimensions[2] - 1
	end_y = start_y + structure_dimensions[1] - 1
	end_x = start_x - structure_dimensions[0] + 1
	return end_x, end_y, end_z


def get_facing_neg_x_coordinates(start_x, start_y, start_z, structure_dimensions):
	end_z = start_z - structure_dimensions[2] + 1
	end_y = start_y + structure_dimensions[1] - 1
	end_x = start_x - structure_dimensions[0] + 1
	return end_x, end_y, end_z


def get_facing_neg_z_coordinates(start_x, start_y, start_z, structure_dimensions):
	end_z = start_z + structure_dimensions[2] - 1
	end_y = start_y + structure_dimensions[1] - 1
	end_x = start_x + structure_dimensions[0] - 1
	return end_x, end_y, end_z


def get_facing_pos_x_coordinates(start_x, start_y, start_z, structure_dimensions):
	end_z = start_z + structure_dimensions[2] - 1
	end_y = start_y + structure_dimensions[1] - 1
	end_x = start_x + structure_dimensions[0] - 1
	return end_x, end_y, end_z


class PlayerPos(object):
	x = 0
	y = 0
	z = 0


if __name__ == '__main__':
	player_pos = PlayerPos()
	player_pos.x = 10
	player_pos.y = 10
	player_pos.z = 10

	get_build_coordinates(player_pos, -177, [3, 3, 3])
