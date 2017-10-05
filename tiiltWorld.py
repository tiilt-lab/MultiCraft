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


def quit():
    sys.exit()

def inputLine(prompt):
    mc.events.clearAll()
    while True:
        chats = mc.events.pollChatPosts()
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
                	mc.postToChat(parsed['verb'])
                	return "t.go(15)"
        time.sleep(0.2)

mc = minecraft.Minecraft()
playerPos = mc.player.getPos()
playerId = mc.getPlayerId()
t = Turtle(mc)

mc.postToChat("Enter python code into chat, type 'quit' to quit.")
i = code.interact(banner="", readfunc=inputLine, local=locals())




