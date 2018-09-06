from mcpi.minecraft import Minecraft
from Text2Int import text2int
mc = Minecraft.create()


def hi_upper():
	mc.postToChat(text2int("forty"))


def blk_upper():
	print("Yes")
