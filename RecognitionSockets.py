from __future__ import print_function
import threading
import logging
import socket
import argparse
import time
import sys
import SpeechToText as Spt
import SocketCommunication as Comm
import Queue
import zmq
from pygaze.display import Display
from pygaze.screen import Screen
from pygaze.eyetracker import EyeTracker

logging.basicConfig(level=logging.DEBUG,
                    format='[%(levelname)s] (%(threadName)-10s) %(message)s',
                    )

global_phrase = ''


parser = argparse.ArgumentParser(description='Specifies options (Watson, PocketSphinx, Text Input) for command input.')
parser.add_argument('--input', dest='input_method', default='text', help='Specify the input method that will be used (default: sphinx)', choices = ['watson', 'sphinx','text'])

args = vars(parser.parse_args())
input_method = args['input_method']

def run_server():
    logging.info('Server started: Ctrl-C to kill')
    try:
        while True:
            pipe, _ = sock.accept()
            pipe.settimeout(0.05)
            Comm.clients.append(pipe)
            logging.debug(Comm.clients)
    except KeyboardInterrupt:
        logging.exception('interrupted')


def cleanup_server():
    sock.close()
    for pipe in Comm.clients:
        pipe.close()


def eye_tracker(lock, event):
    logging.debug('In eye_tracker threading function.')
    global EYE_DATA
    global tracker

    too_big = False
    while True:
        try:
            data = tracker.sample()
            chunk = data
            if chunk:
                if not too_big:
                    EYE_DATA.put(chunk)
                    if EYE_DATA.full():
                        EYE_DATA.get()
                        too_big = True
                else:
                    EYE_DATA.put(chunk)
                    EYE_DATA.get()
        except:
            logging.debug(sys.exc_info()[0])

if __name__ == '__main__':
    HOST = ''   # Symbolic name, meaning all available interfaces
    PORT = 8888 # Arbitrary non-privileged port
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

    logging.debug('Socket created')
    try:
        sock.bind((HOST, PORT))
    except socket.error as msg:
        logging.exception('Bind failed. Error Code : ' + str(msg[0]) + ' Message ' + msg[1])
        sys.exit()

    logging.debug('Socket bind complete')

    # Start listening on socket
    sock.listen(0)
    logging.debug('Socket now listening')

    sd = Spt.SpeechDetector()
    sd.setup_mic()

    t = threading.Thread(target=run_server)
    t.daemon = True

    eye_lock = threading.Lock()
    eye_event = threading.Event()
    eye_thread = threading.Thread(name='eye_tracking', target=eye_tracker, args = (eye_lock, eye_event,))
    eye_thread.daemon = True

    disp = Display(disptype='psychopy')
    scr = Screen(disptype='psychopy')
    disp.close()
    tracker = EyeTracker(disp, trackertype='eyetribe')

    EYE_DATA = Queue.Queue(500)

    try:
        t.start()
        eye_thread.start()
        try:
            exit_cmd = 'exit'
            logging.info('Kill server and exit with "%s"' % exit_cmd)
            while True:
                if(input_method == 'text'):
                    cmd = raw_input('Type a Command: ').strip().lower()
                else:
                    cmd = sd.run(input_method)

                logging.debug(cmd)
                if not cmd:
                    pass
                elif cmd == exit_cmd:
                    break

                if not Comm.interpret_command(cmd, EYE_DATA):
                    logging.exception('bad unrecognized command one "%s"' % cmd)

        except EOFError:
            logging.exception('EOF')
        except KeyboardInterrupt:
            pass
    except Exception as e:
        logging.exception(e)
    finally:
        cleanup_server()

    sock.close()