from __future__ import print_function
import pyautogui
import socket




def socket_gaze_test():

	# Create a TCP/IP socket
	sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

	# Bind the socket to the port
	server_address = ('localhost', 8080)
	print('starting up on {} port {}'.format(*server_address))
	sock.bind(server_address)

	# Listen for incoming connections
	sock.listen(1)

	while True:
		# Wait for a connection
		print('waiting for a connection')
		connection, client_address = sock.accept()
		try:
			print('connection from', client_address)

			# Receive the data in small chunks and retransmit it
			while True:
				data = connection.recv(50)
				# print('received {!r}'.format(data))
				data = data.decode("utf-8").replace(" ","")
				coordinates = data.split(":")
				if len(coordinates) == 2:
					try:
						x_coord = int(float(coordinates[0]))
						y_coord = int(float(coordinates[1]))

						pyautogui.moveTo(x_coord, y_coord)
					except ValueError:
						continue

				if data:
					print('sending data back to the client')
					#connection.sendall(data)
				else:
					print('no data from', client_address)
					break

		finally:
			# Clean up the connection
			connection.close()
