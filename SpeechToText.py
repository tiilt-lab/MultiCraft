#!/usr/bin/env python
#
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


import base64
import configparser
import json
import threading
import time
import pyaudio
import websocket
from websocket._abnf import ABNF
from collections import deque
import os
import wave


class AudioHandler(object):
	def __init__(
			self, rate=16000, channels=2, chunk_size=2048, socket_max_time=600, write_interval=10,
			audio_folder='audio_output'):
		self.CHUNK = chunk_size
		self.FORMAT = pyaudio.paInt16
		self.CHANNELS = channels
		self.RATE = rate
		self.DATA_QUEUE = deque()
		self.audio_stream = None
		self.ws = None
		self.pyaudio_instance = pyaudio.PyAudio()
		self.chunk_index = 0
		self.recording_audio = False
		self.write_interval = write_interval
		self.chunk_count = int(self.RATE / self.CHUNK * self.write_interval)
		self.socket_restart_count = int(self.RATE / self.CHUNK * socket_max_time)
		self.writing_audio_to_file = False
		self.TRANSCRIPT_RESULTS_QUEUE = deque()
		self.AUDIO_QUEUE = deque()
		self.reset_count = 0
		self.session = 0
		self.last_chunk_sent = 0
		self.read_audio()

		self.audio_folder = audio_folder + str(int(time.time()))
		if not os.path.exists(self.audio_folder):
			os.mkdir(self.audio_folder)

	def callback(self, in_data, frame_count, time_info, status):
		self.DATA_QUEUE.append((self.chunk_index, in_data, frame_count, time_info))
		self.AUDIO_QUEUE.append((self.chunk_index, in_data, frame_count, time_info))

		if self.chunk_index % self.chunk_count == self.chunk_count - 1:
			self.writing_audio_to_file = True
		self.chunk_index += 1
		return None, pyaudio.paContinue

	def write_to_file(self):
		while True:
			if self.writing_audio_to_file:
				data_to_write = []
				start_time = None
				for i in range(int(self.chunk_count)):
					if self.AUDIO_QUEUE:
						chunk_index, data, frame_count, time_info = self.AUDIO_QUEUE.popleft()
						if len(data_to_write) == 0:
							start_time = chunk_index
						data_to_write.append(data)

				if len(data_to_write):
					full_file_path = os.path.join(self.audio_folder, str(start_time) + ".wav")
					wf = wave.open(full_file_path, 'wb')
					wf.setnchannels(self.CHANNELS)
					wf.setsampwidth(self.pyaudio_instance.get_sample_size(pyaudio.paInt16))
					wf.setframerate(self.RATE)
					wf.writeframes(b''.join(data_to_write))
					wf.close()

				self.writing_audio_to_file = False

	def read_audio(self):
		self.audio_stream = self.pyaudio_instance.open(
			format=self.FORMAT,
			channels=self.CHANNELS,
			rate=self.RATE,
			start=False,
			input=True,
			frames_per_buffer=self.CHUNK,
			stream_callback=self.callback)
		self.recording_audio = False

	def pass_audio_to_socket(self):
		if self.reset_count == 0:
			self.audio_stream.start_stream()
			print("recording started...")
		self.session += 1
		self.recording_audio = True
		threading.Thread(target=self.write_to_file).start()
		self.reset_count += 1
		while self.ws.sock.connected and (self.audio_stream.is_active() or len(self.DATA_QUEUE) > 0) and (
				self.chunk_index < self.socket_restart_count * self.reset_count):
			if self.DATA_QUEUE:
				(chunk_index, in_data, frame_count, time_info) = self.DATA_QUEUE.popleft()
				self.ws.send(in_data, ABNF.OPCODE_BINARY)
		print("* done recording")
		data = {"action": "stop"}
		self.ws.send(json.dumps(data).encode('utf8'))
		time.sleep(1)
		self.ws.close()
		self.ws = None
		self.setup_websocket()

	def __exit__(self):
		self.audio_stream.stop_stream()
		self.audio_stream.close()
		self.ws.close()
		self.pyaudio_instance.terminate()

	def on_message(self, ws, msg):
		data = json.loads(msg)
		if 'results' in data.keys() and data['results'][0]['final'] == True:
			transcript = data['results'][0]['alternatives'][0]['transcript']
			print(transcript)
			self.TRANSCRIPT_RESULTS_QUEUE.append(transcript)

	def setup_websocket(self):
		headers = {}
		userpass = ":".join(get_auth())
		headers["Authorization"] = "Basic " + base64.b64encode(
			userpass.encode()).decode()
		url = "wss://stream.watsonplatform.net//speech-to-text/api/v1/recognize?model=en-US_BroadbandModel"
		self.ws = websocket.WebSocketApp(
			url,
			header=headers,
			on_message=self.on_message,
			on_error=self.on_error,
			on_close=self.on_close)
		self.ws.on_open = self.on_open

	def on_error(self, ws, error):
		print(error)

	def on_close(self, ws):
		print("Socket Closed")

	def on_open(self, ws):
		print("Websocket Opened")
		data = {
			"action": "start",
			"content-type": "audio/l16;rate=%d;channels=%d" % (self.RATE, self.CHANNELS),
			"interim_results": True,
			"word_confidence": True,
			"timestamps": True,
			"speaker_labels": True,
		}
		self.ws.send(json.dumps(data).encode('utf8'))
		threading.Thread(target=self.pass_audio_to_socket).start()


def get_auth():
	config = configparser.RawConfigParser()
	config.read('speech.cfg')
	user = config.get('auth', 'username')
	password = config.get('auth', 'password')
	return user, password


if __name__ == "__main__":
	a = AudioHandler(channels=2)
	a.setup_websocket()
	a.ws.run_forever()
