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
from MinecraftInterpreter import processInstruction

commands = {'build', 'move', 'turn'}


def executeInstruction(instruction):
    # get dictionary for command and arguments
    instructionDict = processInstruction(instruction)
    mc.postToChat(instructionDict)

    if instructionDict['command'] is None:
        return 'No command was recognized'
    elif instructionDict['command'] not in commands:
        return "The recognized command " + instructionDict['command'] + " is not supported by the system"

    # orient player to match turtle
    t.goto(mc.player.getPos().x, mc.player.getPos().y, mc.player.getPos().z)
    t.angle(mc.player.getRotation())

    if instructionDict['command'] == 'move':
        t.penup()
        if instructionDict['direction'] == 'backward' or instructionDict['ADV'] == 'back':
            t.right(180)
        elif instructionDict['direction'] is 'left':
            t.left(90)
        elif instructionDict['direction'] is 'right':
            t.right(90)
        else:
            #NEED TO MOVE FORWARD AS DEFAULT MAYBE
            return 'Please specify what direction you want the player to move in.'
        t.go(instructionDict['dimensions'][0])
        return 'executed'
    elif instructionDict['command'] == 'build':
        t.gridalign()
        comms = instructionDict['dimensions']
        pos = mc.player.getPos()
        x = pos.x
        y = pos.y
        z = pos.z
        blockCode = instructionDict['block']
        buildingBlocks(x, y, z, comms[0], comms[1], comms[2], blockCode)
        if instructionDict['house'] is False:
            return 'executed'
        elif instructionDict['hollow'] is True:
            x += 1
            y += 1
            z += 1
            comms[0] -= 2
            comms[1] -= 2
            comms[2] -= 1
            buildingBlocks(x, y, z, comms[0], comms[1], comms[2], 0)

        return 'executed'
    elif instructionDict['command'] == 'turn':
        if instructionDict['direction'] == 'backward' or instructionDict['direction'] == 'back':
            t.right(180)
        elif instructionDict['direction'] is 'left':
            t.left(90)
        elif instructionDict['direction'] is 'right':
            t.right(90)
        return 'executed'
    else:
        return 'Command is not supported'


def buildingBlocks(x, y, z, width, height, length, blockType):
    mc.setBlocks(x, y, z, x + width, y + height, z + length, blockType)


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
                    mc.postToChat(c.message)
                    response = executeInstruction(c.message)
                    if (response == 'executed'):
                        mc.postToChat('executed')
                        pass
                    else:
                        mc.postToChat(response)
        time.sleep(0.2)


mc = minecraft.Minecraft()
playerPos = mc.player.getPos()
playerId = mc.getPlayerId()
t = Turtle(mc)

mc.postToChat("Enter python code into chat, type 'quit' to quit.")
i = code.interact(banner="", readfunc=inputLine, local=locals())