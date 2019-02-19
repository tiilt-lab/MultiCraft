from os import environ, path

from pocketsphinx.pocketsphinx import *
from sphinxbase.sphinxbase import *
import pyaudio
import requests

MODELDIR = "pocketsphinx\model"
DATADIR = "pocketsphinx/test/data"

# Create a decoder with certain model
config = Decoder.default_config()
config.set_string('-hmm', 'C:\Users\sespwalkup\Desktop\pocketsphinx\model\en-us\en-us')
config.set_string('-lm', 'C:\Users\sespwalkup\Desktop\pocketsphinx\model\en-us\en-us.lm.bin')
config.set_string('-dict','C:\Users\sespwalkup\Desktop\pocketsphinx\model\en-us\cmudict-en-us.dict')
config.set_string('-keyphrase', 'mario')
# config.set_float('-kws_threshold', 1e-20)
config.set_string('-logfn', 'nul')
decoder = Decoder(config)
# decoder.set_kws('build','keywords.txt')

p = pyaudio.PyAudio()
stream = p.open(format=pyaudio.paInt16, channels=1, rate=16000, input=True, frames_per_buffer=1024)
stream.start_stream()
in_speech_bf = False
decoder.start_utt()

# stream = open('C:\Users\sespwalkup\Desktop\pocketsphinx\\test\data\goforward.raw', 'rb')
i = 0
while True:
	i += 1
	buf = stream.read(1024)
	# if i % 25 == 0:
	# 	print "resetting sphinx"
	# 	decoder.end_utt()
	# 	# decoder.process_raw(buf, False, False)
	# 	decoder.start_utt()
	# 	continue
	if buf:
		decoder.process_raw(buf, False, False)
		if decoder.get_in_speech() != in_speech_bf:
			print 'here'
			in_speech_bf = decoder.get_in_speech()
			if not in_speech_bf:
				decoder.end_utt()
				print decoder.hyp().hypstr
				if 'mario' in decoder.hyp().hypstr: 	
					print "True"
					requests.post('http://localhost:5000/codes?send=true', data={'send':'true'})
				else:
					print "False"
				decoder.start_utt()
	else:
		break

decoder.end_utt()