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

CHUNK = 1024
FORMAT = pyaudio.paInt16
CHANNELS = 1
RATE = 44100
RECORD_SECONDS = 10
FINALS = []
CLIENT_NAME = input("Enter the name of the player: ")
SOCKET = socket.socket(socket.AF_INET, socket.SOCK_STREAM)


def connect_to_server(host, port):
	global SOCKET
	SOCKET.connect((host, port))


def read_audio(ws, timeout):
	global RATE
	p = pyaudio.PyAudio()
	RATE = int(p.get_default_input_device_info()['defaultSampleRate'])
	stream = p.open(
		format=FORMAT,
		channels=CHANNELS,
		rate=RATE,
		input=True,
		frames_per_buffer=CHUNK)

	print("* recording")
	rec = timeout or RECORD_SECONDS

	for i in range(0, int(RATE / CHUNK * rec)):
		data = stream.read(CHUNK)
		ws.send(data, ABNF.OPCODE_BINARY)

	stream.stop_stream()
	stream.close()
	print("* done recording")

	data = {"action": "stop"}
	ws.send(json.dumps(data).encode('utf8'))
	time.sleep(1)
	ws.close()
	p.terminate()


def on_message(self, msg):
	data = json.loads(msg)
	if "results" in data:
		if data["results"][0]["final"]:
			# print(data['results'][0]['alternatives'][0]['transcript'])
			transcript = data['results'][0]['alternatives'][0]['transcript']
			send_string(CLIENT_NAME + " " + transcript)


def send_string(msg):
	global SOCKET
	print("Called send string")
	try:
		SOCKET.send(msg.encode())
	except Exception as er:
		print(er)
	data = SOCKET.recv(1024).decode()
	if data == "server has shut down":
		SOCKET.shutdown(SOCKET.SHUT_RDWR)
		SOCKET.close()
		sys.exit()


def on_error(self, error):
	print(error)


def on_close(ws):
	transcript = "".join(
		[x['results'][0]['alternatives'][0]['transcript'] for x in FINALS])
	print(transcript)


def on_open(ws):
	args = ws.args
	data = {
		"action": "start",
		"content-type": "audio/l16;rate=%d" % RATE,
		"continuous": True,
		"interim_results": True,
		# "inactivity_timeout": 5, # in order to use this effectively
		# you need other tests to handle what happens if the socket is
		# closed by the server.
		"word_confidence": True,
		"timestamps": True,
		"max_alternatives": 3
	}
	ws.send(json.dumps(data).encode('utf8'))
	connect_to_server(args.address, args.port)
	threading.Thread(
		target=read_audio, args=(ws, args.timeout)).start()


def get_auth():
	config = configparser.RawConfigParser()
	config.read('speech.cfg')
	user = config.get('auth', 'username')
	password = config.get('auth', 'password')
	return user, password


def parse_args():
	parser = argparse.ArgumentParser(
		description='Transcribe Watson text in real time')
	parser.add_argument('-t', '--timeout', type=int, default=5)
	parser.add_argument('-a', '--address', type=str, default="localhost")
	parser.add_argument('-p', '--port', type=int, default=8000)
	# parser.add_argument('-d', '--device')
	# parser.add_argument('-v', '--verbose', action='store_true')
	args = parser.parse_args()
	return args


def main():
	headers = {}
	userpass = ":".join(get_auth())
	headers["Authorization"] = "Basic " + base64.b64encode(
		userpass.encode()).decode()
	url = (
		"wss://stream.watsonplatform.net//speech-to-text/api/v1/recognize?model=en-US_BroadbandModel")

	# If you really want to see everything going across the wire,
	# uncomment this.
	# websocket.enableTrace(True)
	ws = websocket.WebSocketApp(
		url,
		header=headers,
		on_message=on_message,
		on_error=on_error,
		on_close=on_close)
	ws.on_open = on_open
	ws.args = parse_args()
	ws.run_forever()


if __name__ == "__main__":
	main()
