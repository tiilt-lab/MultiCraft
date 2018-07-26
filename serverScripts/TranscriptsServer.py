import socket
from threading import *
from collections import deque

keep_server_running = True
client_count = 0
TRANSCRIPTS = deque()


class TranscriptServer(Thread):
	def __init__(self, sock, address):
		Thread.__init__(self, daemon=True)
		self.sock = sock
		self.address = address
		self.start()

	def run(self):
		global keep_server_running, client_count, TRANSCRIPTS
		while True:
			try:
				transcript = self.sock.recv(1024).decode()
				TRANSCRIPTS.append(transcript)
				print(transcript)
				if len(transcript.split()) > 1 and transcript.split()[1] == "quit":
					self.sock.send(b'server has shut down')
					print("Disconnecting from a client")
					client_count -= 1
					break
				else:
					self.sock.send(b'Message received')
			except Exception as er:
				print(er)

		if client_count == 0:
			keep_server_running = False
		print("breaking out of run for this client")
		self.sock.shutdown(socket.SHUT_RDWR)
		self.sock.close()


def run_server():
	global keep_server_running, client_count, TRANSCRIPTS
	server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
	host = "10.105.213.217"
	port = 8000
	server_socket.bind((host, port))
	server_socket.listen(10)
	print("Server is running...")

	while True:
		if not keep_server_running:
			break
		client_socket, address = server_socket.accept()
		TranscriptServer(client_socket, address)
		client_count += 1
		print("stuff")

	print("Exiting the server")
	TRANSCRIPTS.append("Exiting the server")
	server_socket.close()
	print("Server is shutting down")
	TRANSCRIPTS.append("Server is shutting down")
