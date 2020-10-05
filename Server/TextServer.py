import json
import socket
import sys
from threading import Thread
import traceback

from Interpreter import process_instruction


# Connection constants
SENDING_SOCKET = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
HOST = "127.0.0.1"
SEND_PORT = 5003
RECV_PORT = 5001


print('Trying to connect to server...')
try:
    SENDING_SOCKET.connect((HOST, SEND_PORT))
    print("Connected")
except Exception as e:
    print(e)
    print("Socket connection error")
    sys.exit()


def client_thread(connection, keyword):
    """
    A function running on a separate thread that continually receives from 
    the client connection. If a command was sent from the client, interpret 
    it and send the interpretation to the Multicraft server.

    Parameters
    ----------
    connection : socket
        The socket connected to the audiohandler.py client
    keyword : bool
        Whether or not to check for the 'multicraft' command keyword
    """
    split_by = 2 if keyword else 1

    while True:
        client_input = connection.recv(1024).decode(errors='ignore')

        if client_input is not None and len(client_input.split(' ')) > 1:
            client_transcript = client_input.split(' ', split_by)
            
            if keyword and client_transcript[1] != 'multicraft': continue
            
            args = process_instruction(client_transcript[1])
            if args is not None:
                args['client_name'] = client_transcript[0]
                print("sending " + json.dumps(args))
                SENDING_SOCKET.send((json.dumps(args) + "\n").encode())


def main():
    """
    Main function. Attempts to find a client to establish connection.
    Once a connection is found, begins client_thread to handle input 
    commands coming from that connection.
    """
    receiving_socket = socket.socket()
    print("Socket created")

    try:
        receiving_socket.bind(("0.0.0.0", RECV_PORT))
    except Exception as e:
        print(e)
        sys.exit()

    receiving_socket.listen(1) # Number of players to expect in the queue
    print("Socket now listening")

    while True:
        connection, address = receiving_socket.accept()
        ip, port = str(address[0]), str(address[1])
        print("Connection accepted...\nConnected with " + ip + ":" + port)

        try:
            Thread(target=client_thread, args=(connection, False)).start()
        except:
            print("Thread did not start")
            traceback.print_exc()

    if receiving_socket: receiving_socket.close()


if __name__ == "__main__":
    main()
    