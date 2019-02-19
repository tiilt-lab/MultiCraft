from __future__ import print_function
#
# WARNING: If you're running RJM on a server, do NOT include this script server-side for security reasons.
#

#
# Code by Alexander Pruss and under the MIT license
#
#
# Requires Raspberry Jam
#

from ws4py.websocket import WebSocket
from ws4py.server.geventserver import WSGIServer
from ws4py.server.wsgiutils import WebSocketWSGIApplication
import threading
import json
import sys


prev_data = {}
class WS(WebSocket):
    def received_message(self, message):
        message = message.data

        if len(message) > 0:
            message = str(message.decode('utf8'))

            if message == "confirm":
                processTopCodes(prev_data)
            else:
                prev_data = json.loads(message)

def quit():
    sys.exit()

# def calculate_direction1(mc):
#     x_dir = mc.player.getDirection().x
#     z_dir = mc.player.getDirection().z
#     if fabs(x_dir) > fabs(z_dir):
#         if x_dir > 0:
#             player_dir = 'xp'
#         else:
#             player_dir = 'xn'
#     else:
#         if z_dir > 0:
#             player_dir = 'zp'
#         else:
#             player_dir = 'zn'

#     return player_dir

def sortxFn(t):
    return t['x']

def sortyFn(t):
    return t['y']

def same_row(t1, t2, rad):
    return abs(t1['y'] - t2['y']) < 2*rad # if closer than 2 radius distance away

def same_column(t1, t2, rad):
    return abs(t1['x'] - t2['x']) < 2*rad # if closer than 2 radius distance away


def processTopCodes(data):
    topcodes = data["topcodes"]
    if len(topcodes) == 0:
        return

    vert_disp = 0
    options = data['options']

    # TODO: This code checks if the block type is in the list that is supported 

    # for o in options:
    #     if o['option'] == 'type' and o['value'].lower() in blocktype_dict:
    #         blocktype = blocktype_dict[o['value'].lower()]

    
    # TODO: Check for what this does
    # currently only polling for 9x9 grid
    direction = calculate_direction(mc)
    rad = topcodes[0]['radius'] # assuming all radii are similar size
    bottom_sorted = list(sorted(topcodes, key=sortyFn))
    bottom_sorted.reverse()
    left_sorted = list(sorted(topcodes, key=sortxFn))
    bottom = bottom_sorted[0]['y']
    left = left_sorted[0]['x']
    right = left_sorted[-1]['x']
    top = bottom_sorted[-1]['y']

    curr_row = -1
    curr_col = -1

    # first assign rows
    for t in bottom_sorted:
        # mc.postToChat(t)
        if 'r' in t: 
            # already given row value
            continue

        curr_row += 1
        t_samerow = [t_ for t_ in bottom_sorted if same_row(t, t_, rad)]

        # t_samerow should include t
        for t_ in t_samerow:    
            t_['r'] = curr_row
    
    for t in left_sorted:
        if 'c' in t:
            continue

        curr_col += 1
        t_samecol = [t_ for t_ in left_sorted if same_column(t, t_, rad)]

        # t_samecol should include t
        for t_ in t_samecol:
            t_['c'] = curr_col

    offset = max([t['c'] for t in topcodes]) / 2

    for t in topcodes:
        # mc.postToChat(t)
        if 'c' not in t or 'r' not in t:
            mc.postToChat("Problem")
            mc.postToChat(t)
            break

        placeBlock(direction, t['r'], t['c'], mc, offset = offset, blocktype = blocktype, vert_disp = vert_disp)

    mc.postToChat('complete')


# TODO: We will not need this
# def placeBlock(direction, r, c, mc, offset = 0, blocktype = block.DIAMOND_ORE, vert_disp = 0):
#     # building in front of and towards the right as columns increase
#     playerPos = mc.player.getPos()
#     if direction == 'xp':
#         mc.setBlock(playerPos.x + 1, playerPos.y + r + vert_disp, playerPos.z + c - offset, blocktype)
#     if direction == 'xn':
#         mc.setBlock(playerPos.x - 1, playerPos.y + r + vert_disp, playerPos.z - c + offset, blocktype)   
#     if direction == 'zp':
#         mc.setBlock(playerPos.x - c + offset, playerPos.y + r + vert_disp, playerPos.z + 1, blocktype)
#     if direction == 'zn':
#         mc.setBlock(playerPos.x + c - offset, playerPos.y + r + vert_disp, playerPos.z - 1, blocktype) 

def top_codes_loop():
    server = WSGIServer(('localhost', 5050), WebSocketWSGIApplication(handler_cls=WS))
    server.serve_forever()

def main():
    top_codes_thread = threading.Thread(target=top_codes_loop)
    top_codes_thread.start()

if __name__ == "__main__":
    main()