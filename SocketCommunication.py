import json
import socket
import threading
import logging
import time
import logging
import MineCraftInterpreter as Inter
from pprint import pformat
import pickle



def interpret_command(phrase):
    parsed = Inter.parse_phrase(phrase)
    if parsed is None:
        return "Command not recognized"

    return parsed