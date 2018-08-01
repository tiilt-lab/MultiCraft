import socket
from threading import *
from collections import deque
import sys

TRANSCRIPTS = deque()


class TranscriptServer(Thread):
	def __init__(self, sock, address):
		Thread.__init__(self, daemon=True)
		self.sock = sock
		self.address = address
		self.start()

	def run(self):
		global TRANSCRIPTS
		while True:
			try:
				transcript = self.sock.recv(1024).decode()
			except:
				print("Could not receive message from client. Error: " + str(sys.exc_info()))
				self.sock.close()
				sys.exit()

			TRANSCRIPTS.append(transcript)
			print(transcript)
			try:
				self.sock.send(b'Message has been recieved by server')
			except:
				print("Could not send message to server. Error: " + str(sys.exc_info()))


def run_server():
	global TRANSCRIPTS
	server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
	host = "localhost"
	port = 8000

	try:
		server_socket.bind((host, port))
	except:
		print("Bind failed. Error: " + str(sys.exc_info()))
		sys.exit()

	server_socket.listen(1)
	print("Server is running...")

	while True:
		client_socket, address = server_socket.accept()
		TranscriptServer(client_socket, address)

	server_socket.close()
	print("Server is shutting down")


if __name__ == "__main__":
	run_server()
