import socket

HOST = "127.0.0.1"
PORT = 5001

client_socket = socket.socket()
CLIENT_NAME = input("Please enter your screen name: ")
client_socket.connect((HOST, PORT))


while True:
    transcript = input("Please enter a command to send: ")
    transcript = CLIENT_NAME + " " + transcript
    client_socket.send((CLIENT_NAME + " " + transcript).encode())