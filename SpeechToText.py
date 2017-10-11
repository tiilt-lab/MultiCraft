from __future__ import print_function

from watson_developer_cloud import SpeechToTextV1

import os
import pyaudio
import wave
import audioop
from collections import deque
import time
import math

import speech_recognition as sr

"""
Written by Timothy Mwiti, 2017
Adapted from Sophie Li, 2016
http://blog.justsophie.com/python-speech-to-text-with-pocketsphinx/
"""


def speech_2_text(file_name):
    speech_to_text = SpeechToTextV1(
        username='',
        password='',
        x_watson_learning_opt_out=False
    )
    speech_to_text.get_model('en-US_BroadbandModel')
    with open(file_name, 'rb') as audio_file:
        results = speech_to_text.recognize(
            audio_file, content_type='audio/wav', timestamps=True,
            word_confidence=True)
        first_array = results["results"]
        transcript = ''
        for element in first_array:
            transcript += element["alternatives"][0]["transcript"] + ' '

        return transcript

class SpeechDetector:
    def __init__(self):
        # Microphone stream config.
        self.CHUNK = 1024  # CHUNKS of bytes to read each time from mic
        self.FORMAT = pyaudio.paInt16
        self.CHANNELS = 1
        self.RATE = 16000

        self.SILENCE_LIMIT = 2.5  # Silence limit in seconds. The max ammount of seconds where
        # only silence is recorded. When this time passes the
        # recording finishes and the file is decoded

        self.PREV_AUDIO = 0.5  # Previous audio (in seconds) to prepend. When noise
        # is detected, how much of previously recorded audio is
        # prepended. This helps to prevent chopping the beginning
        # of the phrase.

        self.THRESHOLD = 2500
        self.num_phrases = -1

    def setup_mic(self, num_samples=50):
        """ Gets average audio intensity of your mic sound. You can use it to get
            average intensities while you're talking and/or silent. The average
            is the avg of the .2 of the largest intensities recorded.
        """

        print("Getting intensity values from mic.")
        p = pyaudio.PyAudio()
        stream = p.open(format=self.FORMAT,
                        channels=self.CHANNELS,
                        rate=self.RATE,
                        input=True,
                        frames_per_buffer=self.CHUNK)

        values = [math.sqrt(abs(audioop.avg(stream.read(self.CHUNK), 4)))
                  for x in range(num_samples)]
        values = sorted(values, reverse=True)
        r = sum(values[:int(num_samples * 0.2)]) / int(num_samples * 0.2)
        print(" Finished ")
        print(" Average audio intensity is ", r)
        stream.close()
        p.terminate()
        return r


    def save_speech(self, data, p):
        """
        Saves mic data to temporary WAV file. Returns filename of saved
        file
        """
        filename = 'tempfiles/output_' + str(int(time.time()))
        # writes data to WAV file
        data = ''.join(data)
        wf = wave.open(filename + '.wav', 'wb')
        wf.setnchannels(1)
        wf.setsampwidth(p.get_sample_size(pyaudio.paInt16))
        wf.setframerate(self.RATE)  # TODO make this value a function parameter?
        wf.writeframes(data)
        wf.close()
        return filename + '.wav'

    def run(self):
        """
        Listens to Microphone, extracts phrases from it and calls pocketsphinx
        to decode the sound
        """

        # Open stream
        p = pyaudio.PyAudio()
        stream = p.open(format=self.FORMAT,
                        channels=self.CHANNELS,
                        rate=self.RATE,
                        input=True,
                        frames_per_buffer=self.CHUNK)
        print("* Mic set up and listening. ")

        audio2send = []
        cur_data = ''  # current chunk of audio data
        rel = self.RATE / self.CHUNK
        slid_win = deque(maxlen=self.SILENCE_LIMIT * rel)
        # Prepend audio from 0.5 seconds before noise was detected
        prev_audio = deque(maxlen=self.PREV_AUDIO * rel)
        started = False

        while True:
            cur_data = stream.read(self.CHUNK)
            slid_win.append(math.sqrt(abs(audioop.avg(cur_data, 4))))

            if sum([x > self.THRESHOLD for x in slid_win]) > 0:
                if not started:
                    print("Starting recording of phrase")
                    started = True
                audio2send.append(cur_data)

            elif started:
                print("Finished recording, decoding phrase")
                filename = self.save_speech(list(prev_audio) + audio2send, p)
                # r = self.decode_phrase(filename)  # Pocketsphinx Decoder
                #r = self.decode_phrase_sphinx(filename)  # Sphinx Decoder
                
                r = speech_2_text(filename)
                # Removes temp audio file
                os.remove(filename)
                stream.close()
                # Reset all
                print("Ending Loop")
                return r

            else:
                prev_audio.append(cur_data)

        print("* Done listening")
        stream.close()
        p.terminate()


if __name__ == "__main__":
    sd = SpeechDetector()
    sd.setup_mic()
    sd.run()