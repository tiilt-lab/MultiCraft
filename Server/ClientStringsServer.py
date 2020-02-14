import socket
import sys
import traceback
from threading import Thread
from MinecraftInterpreter import process_instruction
import json

sending_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
host = "127.0.0.1"
send_port = 5003

print('Trying to connect to server: ')


try:
    sending_socket.connect((host, send_port))
    print("Connected")
except Exception as e:
    print(e)
    print("Socket connection error")
    sys.exit()

def start_server():
    global host
    port = 5001
    soc = socket.socket()
    print("Socket created")

    try:
        soc.bind(("0.0.0.0", port))
    except Exception as e:
        print(e)
        sys.exit()

    soc.listen(1) # Number of players to expect in the queue
    print("Socket now listening")

    while True:
        connection, address = soc.accept()
        print('Connection accepted...')
        ip, port = str(address[0]), str(address[1])
        print("Connected with " + ip + ":" + port)
        try:
            Thread(target=client_thread, args=(connection, ip, port)).start()
        except:
            print("Thread did not start.")
            traceback.print_exc()

    soc.close()


def client_thread(connection, ip, port):
    is_active = True

    while is_active:
        # For testing purposes, you can force input from the
        # lines of code below.

        # comm_str = input("Please enter a command: ")
        # client_input = ("" + comm_str).encode()

        client_input = connection.recv(1024).decode()

        if client_input is None:
            pass
        elif len(client_input.split(' ')) > 1:
            client_transcript = client_input.split(' ', 1)[1]
            # print(client_transcript)
            args = process_instruction(client_transcript)
            if args is not None:
                # print(args)
                args['client_name'] = client_input.split(' ')[0]
                print("sending " + json.dumps(args))
                sending_socket.send((json.dumps(args) + "\n").encode())

if __name__ == "__main__":
    start_server()