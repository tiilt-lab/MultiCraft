import serial
import requests
from ws4py.client.geventclient import WebSocketClient


ser = serial.Serial('COM8', 57600)

while True:
	if ord(ser.read()) == 1:
		print("sending")

		try:
			ws = WebSocketClient('ws://127.0.0.1:5050')
			ws.connect()
			ws.send('confirm')
		except:
			print("Could not connect to server")

		# requests.get('http://localhost:5000/codes?send=true', data={'confirm':'true'})
		ser.reset_input_buffer()

