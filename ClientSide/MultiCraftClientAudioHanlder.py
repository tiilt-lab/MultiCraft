# Adapted by: Timothy Mwiti (From https://github.com/watson-developer-cloud/python-sdk/blob/master/examples/microphone-speech-to-text.py)
from __future__ import print_function
import pyaudio
from watson_developer_cloud import SpeechToTextV1
from watson_developer_cloud.websocket import RecognizeCallback, AudioSource
from threading import Thread
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
HOST = "127.0.0.1"
PORT = 5001

client_socket = socket.socket()
CLIENT_NAME = input("Please enter your screen name: ")

# Create an instance of AudioSource
audio_source = AudioSource(q, True, True)

# initialize speech to text service
speech_to_text = SpeechToTextV1(
    username='7ffb9f31-2005-4aa1-9aa7-680ae6d1c2b6',
    password='EkKEt3E6ELUs',
    url='https://stream.watsonplatform.net/speech-to-text/api')

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
        print('Service is listening')

    def on_hypothesis(self, hypothesis):
        # print(hypothesis)
        pass

    def on_data(self, data):
        if(data['results'][0]['final']):
            transcript = data['results'][0]['alternatives'][0]['transcript']
            client_socket.send((CLIENT_NAME + " " + transcript).encode())

    def on_close(self):
        client_socket.close()
        print("Connection closed")

# this function will initiate the recognize service and pass in the AudioSource
def recognize_using_weboscket(*args):
    mycallback = MyRecognizeCallback()
    speech_to_text.recognize_using_websocket(audio=audio_source,
                                             content_type='audio/l16; rate=44100',
                                             recognize_callback=mycallback,
                                             interim_results=True)


# define callback for pyaudio to store the recording in queue
def pyaudio_callback(in_data, frame_count, time_info, status):
    try:
        q.put(in_data)
    except Full:
        pass # discard
    return (None, pyaudio.paContinue)

def main():
    # instantiate pyaudio
    # audio = pyaudio.PyAudio()

    # # open stream using callback
    # stream = audio.open(
    #     format=FORMAT,
    #     channels=CHANNELS,
    #     rate=RATE,
    #     input=True,
    #     frames_per_buffer=CHUNK,
    #     stream_callback=pyaudio_callback,
    #     start=False
    # )

    # print("Enter CTRL+C to end recording...")
    # stream.start_stream()

    # try:
    #     recognize_thread = Thread(target=recognize_using_weboscket, args=())
    #     recognize_thread.start()
    #     while True:
    #         pass
    # except KeyboardInterrupt:
    #     # stop recording
    #     audio_source.completed_recording()
    #     stream.stop_stream()
    #     stream.close()
    #     audio.terminate()
    while True:
        input_s = input("Please enter a message to send: ")
        client_socket.send((CLIENT_NAME + " " + input_s).encode())

def tiilt_main():
    client_socket.connect((HOST, PORT))
    main()


if __name__ == "__main__":
    client_socket.connect((HOST, PORT))
    main()
