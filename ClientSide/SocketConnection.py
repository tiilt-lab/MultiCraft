import socket
import MultiCraftClientAudioHanlder as MAH
import EyeTracking as ET
import threading


def socket_conn(address, port):
    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_socket.connect((address, socket))


def main():
    audio_hanlder = MAH.MultiCraftClientAudioHanlder()
    eye_tracker = ET.EyeTracker()
    threading.Thread(
            target=audio_hanlder.main).start()
    