import socket
import sys
import traceback
from threading import Thread


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
    # soc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    # soc.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
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
        client_input = connection.recv(1024)
        if "QUIT" in client_input.decode():
            print("Client is requesting to quit")
            connection.close()
            print("Connection " + ip + ":" + port + " closed")
            is_active = False
        else:
            sending_socket.send((client_input.decode() + "\n").encode())
            print(client_input.decode())

def process_input(input_str):
    print("Processing the input received from client")
    return "Hello " + str(input_str).lower()

if __name__ == "__main__":
    main()

