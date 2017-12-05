#STRUCTURE OF RETURN DICTIONARY


#MOVEMENT
#Move two steps forward
#Move to position x,y,z

#BUILDING
#Solid structure: Build a 6 by 6 by 6 glass structure here
#Building a house structure: Build a 6 by 6 by 6 glass house e here


#TURNING
#Turn to the left
#Turn 30 degrees to the right

from tiiltBlocks import getBlockCode

def processInstruction(instruction):
    dict1 = {}
    words = instruction.split()
    if 'move' in words:
        dict1['command'] = words[0]
        dict1['quantity'] = int(words[1])
        dict1['direction'] = words[3]
        return dict1
    elif 'build' in words:
        dict1['command'] = words[0]
        dict1['quantity'] = [int(words[2]),int(words[4]),int(words[6])]
        dict1['hollow'] = False
        dict1['material'] = getBlockCode(words[7].upper())
        if 'house' in words:
            dict1['hollow'] = True
        return dict1
    elif 'turn' in words:
        dict1['command'] = 'turn'
        dict1['direction'] = words[1]
        dict1['angle'] = int(words[2])
    else:
        return dict1