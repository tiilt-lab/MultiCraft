import json
import socket
import tkinter as tk
import urllib.request
import uuid
from queue import Queue, Full
from threading import Thread

# import pyaudio
# from ibm_cloud_sdk_core.authenticators import IAMAuthenticator
# from ibm_watson import SpeechToTextV1
# from ibm_watson.websocket import RecognizeCallback, AudioSource

# from EyeTracker import process_eye_tracking

# # PyAudio Configuration
# CHUNK = 1024
# BUF_MAX_SIZE = CHUNK * 10
# q = Queue(maxsize=int(round(BUF_MAX_SIZE / CHUNK)))
# FORMAT = pyaudio.paInt16
# CHANNELS = 1
# RATE = 44100

# # Create an instance of AudioSource
# audio_source = AudioSource(q, True, True)

def get_uuid(mc_username):
    global CLIENT_NAME
    with urllib.request.urlopen(f'https://api.mojang.com/users/profiles/minecraft/{mc_username}') as response:
        mc_profile = response.read().decode('utf-8')
    try:
        mc_username = json.loads(mc_profile)['name'] # ensures username is case-corrected
        CLIENT_NAME = str(uuid.UUID(json.loads(mc_profile)['id']))
        return f'Connected as {mc_username} ({CLIENT_NAME})'
    except:
        return 'Unable to retrieve UUID: invalid username or API is down'

def connect_to_server(server_ip):
    global CLIENT_SOCKET
    HOST = server_ip
    PORT = 5001
    CLIENT_SOCKET = socket.socket()
    try:
        CLIENT_SOCKET.connect((HOST, PORT))
        return f'Connected to {HOST}:{PORT}'
    except:
        return f'Unable to connect: IP may be incorrect'

# def connect_to_voice():
#     global SPEECH_TO_TEXT
#     with open('./client_credentials.json'):
#         credentials = json.load(f)
#         API_KEY = credentials['api_key']
#         SERVICE_URL = credentials['service_url']
#         CUSTOMIZATION_ID = credentials['customization_id']
#     authenticator = IAMAuthenticator(API_KEY)
#     SPEECH_TO_TEXT = SpeechToTextV1(authenticator=authenticator)
#     SPEECH_TO_TEXT.set_service_url(SERVICE_URL)

# # Define callback for the Speech to Text service
# class MyRecognizeCallback(RecognizeCallback):
#     def __init__(self):
#         RecognizeCallback.__init__(self)

#     def on_transcription(self, transcript):
#         print(transcript)

#     def on_connected(self):
#         print('Connection was successful')

#     def on_error(self, error):
#         print(f'Error received: {error}')

#     def on_inactivity_timeout(self, error):
#         print(f'Inactivity timeout: {error}')

#     def on_listening(self):
#         print('Service is listening\nEnter CTRL+C to end recording...')

#     def on_hypothesis(self, hypothesis):
#         pass

#     def on_data(self, data):
#         # Once received a command, print and send the command string to the server
#         if(data['results'][0]['final']):
#             transcript = data['results'][0]['alternatives'][0]['transcript'].lower()
#             print(transcript)
#             process_eye_tracking(transcript)
#             CLIENT_SOCKET.send((CLIENT_NAME + " " + result).encode())

#     def on_close(self):
#         client_socket.close()
#         print('Connection closed')

# # Initiate the recognize service and pass in the AudioSource
# def recognize_using_websocket(*args):
#     mycallback = MyRecognizeCallback()

#     SPEECH_TO_TEXT.recognize_using_websocket(audio=audio_source,
#                                              content_type='audio/l16; rate=44100',
#                                              recognize_callback=mycallback,
#                                              language_customization_id=CUSTOMIZATION_ID,
#                                              customization_weight=0.9,
#                                              interim_results=True)

# # Define callback for PyAudio to store the recording in queue
# def pyaudio_callback(in_data, frame_count, time_info, status):
#     try:
#         q.put(in_data)
#     except Full:
#         pass # discard
#     return (None, pyaudio.paContinue)

class Frame1():
    def __init__(self, parent):
        self.parent = parent
        self.frame = tk.Frame(self.parent)
        self.label = tk.Label(master=self.frame, text='What is your Minecraft username?')
        self.entry = tk.Entry(master=self.frame)
        self.button = tk.Button(master=self.frame, text='OK', command=self.get_username)
        self.quit_btn = tk.Button(master=self.frame, text='Quit', command=parent.destroy)
        self.label.pack()
        self.entry.pack()
        self.button.pack()
        self.quit_btn.pack()
        self.frame.pack()

    def get_username(self):
        username = self.entry.get()
        message = get_uuid(username)
        if 'Connected' in message:
            self.close_frame()
            msg_label = tk.Label(text=message)
            msg_label.pack() # outside of frame
            frame2 = Frame2(root)
        else:
            self.msg_label = tk.Label(master=self.frame, text=message)
            self.msg_label.pack()
    
    def close_frame(self):
        self.frame.destroy()

class Frame2():
    def __init__(self, parent):
        self.parent = parent
        self.frame = tk.Frame(self.parent)
        self.label = tk.Label(master=self.frame, text='Server IP:')
        self.entry = tk.Entry(master=self.frame)
        self.button = tk.Button(master=self.frame, text='OK', command=self.get_ip)
        self.quit_btn = tk.Button(master=self.frame, text='Quit', command=parent.destroy)
        self.label.pack()
        self.entry.pack()
        self.button.pack()
        self.quit_btn.pack()
        self.frame.pack()
    
    def get_ip(self):
        ip = self.entry.get()
        message = connect_to_server(ip)
        if 'Connected' in message:
            self.close_frame()
            msg_label = tk.Label(text=message)
            msg_label.pack() # outside of frame
        else:
            self.msg_label = tk.Label(master=self.frame, text=message)
            self.msg_label.pack()

    def close_frame(self):
        self.frame.destroy()

root = tk.Tk()
frame1 = Frame1(root)
root.mainloop()
