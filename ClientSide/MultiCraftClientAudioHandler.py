# Adapted by: Timothy Mwiti (From https://github.com/watson-developer-cloud/python-sdk/blob/master/examples/microphone-speech-to-text.py)
from __future__ import print_function
import pyaudio
from ibm_watson import SpeechToTextV1
from ibm_watson.websocket import RecognizeCallback, AudioSource
from threading import Thread
from ibm_cloud_sdk_core.authenticators import IAMAuthenticator
import socket

try:
    from Queue import Queue, Full
except ImportError:
    from queue import Queue, Full

CHUNK = 1024
BUF_MAX_SIZE = CHUNK * 10
q = Queue(maxsize=int(round(BUF_MAX_SIZE / CHUNK)))
TRANSCRIPTS_QUEUE = Queue(maxsize=30)
FORMAT = pyaudio.paInt16
CHANNELS = 1
RATE = 44100
HOST = socket.gethostbyname(socket.gethostname()) # input("Please enter your host ip: ")
PORT = 5001 # input("Please enter your desired port: ")

client_socket = socket.socket()
CLIENT_NAME = input("Please enter your Minecraft UUID: ")

# Create an instance of AudioSource
audio_source = AudioSource(q, True, True)

# initialize speech to text service
API_KEY = input("Please enter your IBM Speech to Text API key: ")
if API_KEY != '':
    SERVICE_URL = input("Please enter your service url: ")
    authenticator = IAMAuthenticator(API_KEY)
    speech_to_text = SpeechToTextV1(authenticator=authenticator)
    speech_to_text.set_service_url(SERVICE_URL)

# define callback for the speech to text service
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
        if(data['results'][0]['final']):
            transcript = data['results'][0]['alternatives'][0]['transcript']
            print(transcript)
            client_socket.send((CLIENT_NAME + " " + transcript).encode())

    def on_close(self):
        client_socket.close()
        print("Connection closed")

# this function will initiate the recognize service and pass in the AudioSource
def recognize_using_weboscket(*args):
    mycallback = MyRecognizeCallback()
    language_customization_id = input("Please enter your language customization id: ")
    customization_weight = 0.9
    if language_customization_id == '':
        customization_weight = language_customization_id = None

    speech_to_text.recognize_using_websocket(audio=audio_source,
                                             content_type='audio/l16; rate=44100',
                                             recognize_callback=mycallback,
                                             language_customization_id=language_customization_id,
                                             customization_weight=customization_weight,
                                             interim_results=True)

# define callback for pyaudio to store the recording in queue
def pyaudio_callback(in_data, frame_count, time_info, status):
    try:
        q.put(in_data)
    except Full:
        pass # discard
    return (None, pyaudio.paContinue)

def main():
    if API_KEY != '':
        # instantiate pyaudio
        audio = pyaudio.PyAudio()

        # open stream using callback
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

        try:
            recognize_thread = Thread(target=recognize_using_weboscket, args=())
            recognize_thread.start()
            while True:
                pass
        except KeyboardInterrupt:
            # stop recording
            stream.stop_stream()
            stream.close()
            audio.terminate()
            audio_source.completed_recording()
    
    else:
        # if speech to text was not initialized, enter testing mode
        while True:
            input_s = input("Please type out a message to send: ")
            client_socket.send((CLIENT_NAME + " " + input_s).encode())

if __name__ == "__main__":
    client_socket.connect((HOST, PORT))
    main()
