# Copyright 2016 IBM
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may
# not use this file except in compliance with the License. You may obtain
# a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations
# under the License.

# Adapted by Timothy Mwiti 2018

import argparse
import base64
import configparser
import json
import threading
import time
import pyaudio
import websocket
from websocket._abnf import ABNF
import socket
import sys
from collections import deque


class MultiCraftClientAudioHanlder(object):
    def __init__(
                self, chunk_size=1024, format=pyaudio.paInt16, channels=1,
                record_seconds=5, rate=44100):
        self.CHUNK = chunk_size
        self.FORMAT = format
        self.CHANNELS = channels
        self.RATE = rate
        self.RECORD_SECONDS = record_seconds
        self.CLIENT_NAME = None
        self.TRANSCRIPTS_QUEUE = deque()
        self.audio_stream = None
        self.pyaudio_instance = pyaudio.PyAudio()
        self.DATA_QUEUE = deque()
        self.chunk_index = 0
        self.ws = None
        self.read_audio()

    def read_audio(self):
        self.audio_stream = self.pyaudio_instance.open(
            format=self.FORMAT,
            channels=self.CHANNELS,
            rate=self.RATE,
            start=False,
            input=True,
            frames_per_buffer=self.CHUNK,
            stream_callback=self.callback
        )
    
    def callback(self, in_data, frame_count, time_info, status):
        self.DATA_QUEUE.append((
            self.chunk_index, in_data, frame_count, time_info))
        self.chunk_index += 1
        return None, pyaudio.paContinue

    def pass_audio_to_socket(self):
        print('recording started...')
        self.audio_stream.start_stream()
        while (self.ws.sock.connected and
                (self.audio_stream.is_active() or len(self.DATA_QUEUE) > 0)):
            if self.DATA_QUEUE:
                in_data = self.DATA_QUEUE.popleft()[1]
                self.ws.send(in_data, ABNF.OPCODE_BINARY)

    def on_message(self, ws, msg):
        data = json.loads(msg)
        if "results" in data.keys() and data["results"][0]["final"]:
                transcript = data['results'][0]['alternatives'][0]['transcript']
                print(transcript)
                self.TRANSCRIPTS_QUEUE.append(transcript)

    def on_error(self, ws, error):
        print(error)

    def on_close(self, ws):
        print('Socket closed...')

    def __exit__(self):
        self.audio_stream.stop_stream()
        self.audio_stream.close()
        self.ws.close()
        self.pyaudio_instance.terminate()

    def on_open(self, ws):
        print('Websocket opened...')
        data = {
            "action": "start",
            "content-type": "audio/l16;rate=%d;channels=%d" %
            (self.RATE, self.CHANNELS),
            # "continuous": True,
            "interim_results": True,
            # "inactivity_timeout": 5, # in order to use this effectively
            # you need other tests to handle what happens if the socket is
            # closed by the server.
            "word_confidence": True,
            "timestamps": True,
            # "max_alternatives": 3
            "speaker_labels": True,
        }
        self.ws.send(json.dumps(data).encode('utf8'))
        threading.Thread(
            target=self.pass_audio_to_socket).start()

    def get_auth(self):
        config = configparser.RawConfigParser()
        config.read('speech.cfg')
        user = config.get('auth', 'username')
        password = config.get('auth', 'password')
        return user, password

    def parse_args(self):
        parser = argparse.ArgumentParser(
            description='Transcribe Watson text in real time')
        parser.add_argument('-t', '--timeout', type=int, default=5)
        # parser.add_argument('-d', '--device')
        # parser.add_argument('-v', '--verbose', action='store_true')
        args = parser.parse_args()
        return args

    def main(self):
        headers = {}
        userpass = ":".join(self.get_auth())
        headers["Authorization"] = "Basic " + base64.b64encode(
            userpass.encode()).decode()
        url = (
            "wss://stream.watsonplatform.net//speech-to-text/\
            api/v1/recognize?model=en-US_BroadbandModel")
        # If you really want to see everything going across the wire,
        # uncomment this.
        # websocket.enableTrace(True)
        self.ws = websocket.WebSocketApp(
            url,
            header=headers,
            on_message=self.on_message,
            on_error=self.on_error,
            on_close=self.on_close)
        self.ws.on_open = self.on_open


if __name__ == "__main__":
    audio_handler = MultiCraftClientAudioHanlder()
    audio_handler.main()
    audio_handler.ws.run_forever()
