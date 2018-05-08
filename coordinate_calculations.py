def get_build_coordinates(player_pos, player_angle, structure_dimensions):
	start_x = player_pos.x
	start_y = player_pos.y
	start_z = player_pos.z

	if(player_angle >= -45 and player_angle <= 45):
		end_x, end_y, end_z = get_facing_pos_z_coordinates(start_x, start_y, start_z, structure_dimensions)
	elif player_angle > 45 and player_angle <=135:
		end_x, end_y, end_z = get_facing_neg_x_coordinates(start_x, start_y, start_z, structure_dimensions)
	elif player_angle > 135 or player_angle <= -135:
		end_x, end_y, end_z = get_facing_neg_z_coordinates(start_x, start_y, start_z, structure_dimensions)
	else:
		end_x, end_y, end_z = get_facing_pos_x_coordinates(start_x, start_y, start_z, structure_dimensions)
	
	return start_x, start_y, start_z, end_x, end_y, end_z

def get_facing_pos_z_coordinates(start_x, start_y, start_z, structure_dimensions):
	end_z = start_z + structure_dimensions[2]
	end_y = start_y + structure_dimensions[1]
	end_x = start_x - structure_dimensions[0]
	return end_x, end_y, end_z


def get_facing_neg_x_coordinates(start_x, start_y, start_z, structure_dimensions):
	end_z = start_z - structure_dimensions[2]
	end_y = start_y + structure_dimensions[1]
	end_x = start_x - structure_dimensions[0]
	return end_x, end_y, end_z

def get_facing_neg_z_coordinates(start_x, start_y, start_z, structure_dimensions):
	end_z = start_z - structure_dimensions[2]
	end_y = start_y + structure_dimensions[1]
	end_x = start_x + structure_dimensions[0]
	return end_x, end_y, end_z

def get_facing_pos_x_coordinates(start_x, start_y, start_z, structure_dimensions):
	end_z = start_z + structure_dimensions[2]
	end_y = start_y + structure_dimensions[1]
	end_x = start_x + structure_dimensions[0]
	return end_x, end_y, end_z