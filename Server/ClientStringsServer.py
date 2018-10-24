import socket
import sys
import traceback
from threading import Thread
from MinecraftInterpreter import process_instruction
import json

sending_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
host = "127.0.0.1"
port = 5003

try:
    sending_socket.connect((host, port))
    print("Connected")
except:
    print("Sending strings socket connection error")
    sys.exit()

def main():
    start_server()

def start_server():
    global host
    port = 5001
    soc = socket.socket()
    print("Socket created")

    try:
        soc.bind((host, port))
    except:
        print("Bind failed. Error : " + str(sys.exc_info()))
        sys.exit()

    soc.listen(5) # queue up to 5 requests
    print("Socket now listening")

    # infinite loop- do not reset for every requests
    while True:
        connection, address = soc.accept()
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
        try:
            client_input = connection.recv(1024)
            if "quit" in client_input.decode():
                connection.close()
                print("Connection " + ip + ":" + port + " closed")
                is_active = False
            else:
                client_transcript = client_input.decode().split(' ', 1)[1]
                if len(client_transcript) > 0:
                    args = process_instruction(client_transcript)
                    args['client_name'] = client_input.decode().split(' ')[0]
                    print("sending " + json.dumps(args))
                    sending_socket.send((json.dumps(args) + "\n").encode())
        except:
            pass

def process_input(input_str):
    print("Processing the input received from client")
    return "Hello " + str(input_str).lower()

if __name__ == "__main__":
    main()
