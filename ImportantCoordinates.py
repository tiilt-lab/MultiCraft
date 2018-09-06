# Open the File, Create File if it does not exist
# locations_file = open('my_locations.txt', 'r+')


def load_location_dict(file_to_read = 'important_locations.txt', locations_dict={}):
	with open(file_to_read) as f:
		locations = f.readlines()
	locations = [x.strip() for x in locations]
	for location in locations:
		location_a = location.strip().split(':')
		coordinates = location_a[1].strip()
		coordinates = coordinates[1:len(coordinates)-1].split(',')
		coordinates = [int(coordinates[0]), int(coordinates[1]), int(coordinates[2])]
		locations_dict[location_a[0]] = coordinates
	return locations_dict


def add_location_to_database(location_name, location_coord, db_file):
	fl = open(db_file, "a")
	fl.write(location_name + ':' + str(location_coord) + "\n")
	fl.close()
