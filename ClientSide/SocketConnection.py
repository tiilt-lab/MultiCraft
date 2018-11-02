import socket
import MultiCraftClientAudioHanlder as MAH
import threading


def socket_conn():
    print("in here")
    host = "127.0.0.1"
    port = 5000
    server_socket = socket.socket()
    server_socket.bind((host, port))
    server_socket.listen(1)
    conn, addr = server_socket.accept()
    print("Connection from: " + str(addr))
    while True:
        data = conn.recv(1024).decode()
        if not data:
            continue
        print("from connected user: " + str(data))

    conn.close()

def main():
    threading.Thread(target=socket_conn).start()
    # threading.Thread(target=MAH.tiilt_main).start()
    

main()