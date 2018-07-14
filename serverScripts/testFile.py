from minecraft import Minecraft

def hi():
    "_mcp: just saying hello"
    mc = Minecraft.create()
    mc.postToChat("Hello!")

def blk():
    "_mcp: place 10 blocks of diamond"
    mc = Minecraft.create()
    pos = mc.player.getTilePos()
    for i in range(10):
        mc.setBlock(pos.x+1, pos.y+i, pos.z, 57)