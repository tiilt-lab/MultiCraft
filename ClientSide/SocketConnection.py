import socket


def socket_conn(address, port):
    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_socket.connect((address, socket))
    while True:
        data = getStringFromQueue()
        client_socket.send((data + '\n').encode())
