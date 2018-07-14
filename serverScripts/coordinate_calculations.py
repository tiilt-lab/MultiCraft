def get_build_coordinates(player_pos, player_angle, structure_dimensions):
	start_x = player_pos.x
	start_y = player_pos.y
	start_z = player_pos.z

	direction = get_general_direction(player_angle)

	if direction == "north":
		end_x, end_y, end_z = get_facing_north_coordinates(start_x, start_y, start_z, structure_dimensions)
		direction = direction + "north"
	elif direction == "east":
		end_x, end_y, end_z = get_facing_east_coordinates(start_x, start_y, start_z, structure_dimensions)
		direction = direction + "east"
	elif direction == "south":
		end_x, end_y, end_z = get_facing_south_coordinates(start_x, start_y, start_z, structure_dimensions)
		direction = direction + "south"
	else:
		end_x, end_y, end_z = get_facing_west_coordinates(start_x, start_y, start_z, structure_dimensions)
		direction = direction + "west"

	return start_x, start_y, start_z, end_x, end_y, end_z, direction


def get_hollow_dimensions(start_x, start_y, start_z, end_x, end_y, end_z, player_angle, structure_dimensions):
	direction = get_general_direction(player_angle)

	if structure_dimensions[1] >= 3:
		start_y += 1
		end_y -= 1

	# fix x and z coordinates
	if direction == "west":  # ok
		start_x -= 1
		end_x += 1
		start_z += 1
		end_z -= 1
	elif direction == "north":
		start_x -= 1
		end_x += 1
		start_z -= 1
		end_z += 1
	elif direction == "east":  # ok
		start_x += 1
		end_x -= 1
		start_z -= 1
		end_z += 1
	else:  # ok
		start_x += 1
		end_x -= 1
		start_z += 1
		end_z -= 1

	return start_x, start_y, start_z, end_x, end_y, end_z


def get_facing_north_coordinates(start_x, start_y, start_z, structure_dimensions):
	end_z = start_z - structure_dimensions[0] + 1
	end_y = start_y + structure_dimensions[1] - 1
	end_x = start_x - structure_dimensions[2] + 1
	return end_x, end_y, end_z


def get_facing_east_coordinates(start_x, start_y, start_z, structure_dimensions):  # fixed
	end_z = start_z - structure_dimensions[2] + 1
	end_y = start_y + structure_dimensions[1] - 1
	end_x = start_x + structure_dimensions[0] - 1
	return end_x, end_y, end_z


def get_facing_south_coordinates(start_x, start_y, start_z, structure_dimensions):
	end_z = start_z + structure_dimensions[0] - 1
	end_y = start_y + structure_dimensions[1] - 1
	end_x = start_x + structure_dimensions[2] - 1
	return end_x, end_y, end_z


def get_facing_west_coordinates(start_x, start_y, start_z, structure_dimensions):  # fixed
	end_z = start_z + structure_dimensions[2] - 1  # not subtracting makes it longer by one
	end_y = start_y + structure_dimensions[1] - 1
	end_x = start_x - structure_dimensions[0] + 1  # subtracting makes it longer by one.
	return end_x, end_y, end_z


def get_general_direction(player_yaw):
	specific_direction = get_specific_direction(player_yaw)
	north_dir = ["north", "north northeast", "north northwest", "northeast"]
	east_dir = ["east", "east northeast", "east southeast", "southeast"]
	south_dir = ["south", "south southeast", "south southwest", "southwest"]
	# west_dir = ["west", "west northwest", "west southwest", "northwest"]

	if specific_direction in north_dir:
		return "north"
	elif specific_direction in east_dir:
		return "east"
	elif specific_direction in south_dir:
		return "south"
	else:
		return "west"


def get_specific_direction(player_yaw):
	if player_yaw < 0:
		player_yaw += 360

	dir_int = int((player_yaw + 8) / 22.5)

	if dir_int == 0:
		return "west"
	elif dir_int == 1:
		return "west northwest"
	elif dir_int == 2: # west
		return "northwest"
	elif dir_int == 3:
		return "north northwest"
	elif dir_int == 4:
		return "north"
	elif dir_int == 5:
		return "north northeast"
	elif dir_int == 6: # north
		return "northeast"
	elif dir_int == 7:
		return "east northeast"
	elif dir_int == 8:
		return "east"
	elif dir_int == 9:
		return "east southeast"
	elif dir_int == 10: # east
		return "southeast"
	elif dir_int == 11:
		return "south southeast"
	elif dir_int == 12:
		return "south"
	elif dir_int == 13:
		return "south southwest"
	elif dir_int == 14:
		return "southwest"
	elif dir_int == 15:
		return "west southwest"
	else:
		return "west"


class PlayerPos(object):
	x = 0
	y = 0
	z = 0


if __name__ == '__main__':
	player_pos = PlayerPos()
	player_pos.x = 10
	player_pos.y = 10
	player_pos.z = 10

	get_build_coordinates(player_pos, 0, [3, 3, 3])
