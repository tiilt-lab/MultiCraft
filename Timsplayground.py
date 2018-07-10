from __future__ import print_function
import socket
import subprocess
import platform


def socket_gaze_test():
	if platform.system() == 'Windows':
		print('Starting eye gaze tracking...')
		try:
			eye_gaze = subprocess.Popen("eye_gaze\Interaction_Streams_101.exe", shell=False)
		except Exception as e:
			raise e

	sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

	# Bind the socket to the port
	server_address = ('localhost', 8080)
	print('starting up on {} port {}'.format(*server_address))
	sock.bind(server_address)

	# Listen for incoming connections
	sock.listen(1)

	while True:
		print('waiting for a connection')
		connection, client_address = sock.accept()
		try:
			print('connection from', client_address)
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
						if print_count % 200 == 0:
							print("You are looking at %s , %s" % (x_coord, y_coord))
					except ValueError:
						continue
				if data:
					continue
				else:
					print('No data from', client_address)
					break

		finally:
			# Clean up the connection
			connection.close()


if __name__ == "__main__":
	socket_gaze_test()
