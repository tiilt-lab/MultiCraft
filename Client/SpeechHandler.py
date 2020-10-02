# Adapted by: Kevin Mendoza Tudares (From https://github.com/watson-developer-cloud/python-sdk/blob/master/examples/microphone-speech-to-text.py)
from __future__ import print_function

import json
import socket
import urllib.request
from threading import Thread

import pyaudio
from ibm_watson import SpeechToTextV1
from ibm_watson.websocket import RecognizeCallback, AudioSource
from ibm_cloud_sdk_core.authenticators import IAMAuthenticator

try:
    from Queue import Queue, Full
except ImportError:
    from queue import Queue, Full


# PyAudio Configuration
CHUNK = 1024
BUF_MAX_SIZE = CHUNK * 10
q = Queue(maxsize=int(round(BUF_MAX_SIZE / CHUNK)))
TRANSCRIPTS_QUEUE = Queue(maxsize=30)
FORMAT = pyaudio.paInt16
CHANNELS = 1
RATE = 44100


# Create an instance of AudioSource
audio_source = AudioSource(q, True, True)


# Connection to Server
HOST = input("Multicraft Server IP: ").split(':')[0] # use socket.gethostbyname(socket.gethostname()) if you are also hosting
PORT = 5001
CLIENT_SOCKET = socket.socket()
mc_username = input("Your Full Minecraft Username: ")
with urllib.request.urlopen(f"https://api.mojang.com/users/profiles/minecraft/{mc_username}") as response:
    mc_profile = response.read().decode("utf-8")
try:
    CLIENT_NAME = json.loads(mc_profile)["id"]
    print(f"Connecting as {CLIENT_NAME}")
except:
    print("Unable to retrieve UUID: invalid username or no response received")
    exit(1)


# Initialize IBM Watson Speech to Text service if user wants to use voice
USING_VOICE = input("Are you using voice [y/n]? ").lower() == 'y'
if USING_VOICE:
    # Speech to Text service credeneitals are stored in ./client_credentials.json
    f = open('./client_credentials.json')
    credentials = json.load(f)

    API_KEY = credentials['api_key']
    SERVICE_URL = credentials['service_url']
    CUSTOMIZATION_ID = credentials['customization_id']

    f.close()

    authenticator = IAMAuthenticator(API_KEY)
    SPEECH_TO_TEXT = SpeechToTextV1(authenticator=authenticator)
    SPEECH_TO_TEXT.set_service_url(SERVICE_URL)


# Define callback for the Speech to Text service
class MyRecognizeCallback(RecognizeCallback):
    def __init__(self):
        RecognizeCallback.__init__(self)

    def on_transcription(self, transcript):
        print(transcript)

    def on_connected(self):
        print('Connection was successful')

    def on_error(self, error):
        print('Error received: {}'.format(error))

    def on_inactivity_timeout(self, error):
        print('Inactivity timeout: {}'.format(error))

    def on_listening(self):
        print('Service is listening\nEnter CTRL+C to end recording...')

    def on_hypothesis(self, hypothesis):
        pass

    def on_data(self, data):
        # Once received a command, print and send the command string to the server
        if(data['results'][0]['final']):
            transcript = data['results'][0]['alternatives'][0]['transcript']
            print(transcript)
            CLIENT_SOCKET.send((CLIENT_NAME + " " + transcript).encode())

    def on_close(self):
        client_socket.close()
        print("Connection closed")


# Initiate the recognize service and pass in the AudioSource
def recognize_using_weboscket(*args):
    mycallback = MyRecognizeCallback()

    SPEECH_TO_TEXT.recognize_using_websocket(audio=audio_source,
                                             content_type='audio/l16; rate=44100',
                                             recognize_callback=mycallback,
                                             language_customization_id=CUSTOMIZATION_ID,
                                             customization_weight=0.9,
                                             interim_results=True)


# Define callback for PyAudio to store the recording in queue
def pyaudio_callback(in_data, frame_count, time_info, status):
    try:
        q.put(in_data)
    except Full:
        pass # discard
    return (None, pyaudio.paContinue)


def main():
    if USING_VOICE:
        # If the user wants to use voice...
        # Instantiate PyAudio
        audio = pyaudio.PyAudio()

        # Open stream using callback
        stream = audio.open(
            format=FORMAT,
            channels=CHANNELS,
            rate=RATE,
            input=True,
            frames_per_buffer=CHUNK,
            stream_callback=pyaudio_callback,
            start=False
        )
        stream.start_stream()

        # Run recognize service thread
        try:
            recognize_thread = Thread(target=recognize_using_weboscket, args=())
            recognize_thread.start()
            while True:
                pass
        # Stop recording when user enters Ctrl+C
        except KeyboardInterrupt:
            print("Recording stopped")
            print("To start another recording, type: python audiohandler.py")
            stream.stop_stream()
            stream.close()
            audio.terminate()
            audio_source.completed_recording()
    
    else:
        # If the user does not want to use voice and instead type commands, enter testing mode
        while True:
            input_s = input("Message: ")
            CLIENT_SOCKET.send((CLIENT_NAME + " " + input_s).encode())


if __name__ == "__main__":
    CLIENT_SOCKET.connect((HOST, PORT))
    main()
