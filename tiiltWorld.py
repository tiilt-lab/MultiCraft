import mcpi.minecraft as minecraft
import time
import math
from math import *
from mcpi.block import *
from mcturtle import *
import code
import sys
sys.path.append('.')
import socket
import json
import pickle
import numpy
import unicodedata
import SocketCommunication as Comm
import SpeechToText as Spt


def quit():
    sys.exit()

def inputLine(prompt):
    mc.events.clearAll()
    sd = Spt.SpeechDetector()
    sd.setup_mic()
    while True:
        chats = mc.events.pollChatPosts()
        cmd = sd.run()

        if chats:
            for c in chats:
                if c.entityId == playerId:
                    if c.message == 'quit':
                        return 'quit()'
                    elif c.message == ' ':
                        return ''
                    elif "__" in c.message:
                        sys.exit();
                    else:
                    	parsed = Comm.interpret_command(c.message)
                    	mc.postToChat(parsed)
                    	return "t.go(15)"
        elif cmd is not None:
            parsed = Comm.interpret_command(cmd)
            mc.postToChat(parsed)
            return "t.go(10)"
            
        time.sleep(0.2)

mc = minecraft.Minecraft()
playerPos = mc.player.getPos()
playerId = mc.getPlayerId()
t = Turtle(mc)

mc.postToChat("Enter python code into chat, type 'quit' to quit.")
i = code.interact(banner="", readfunc=inputLine, local=locals())




