from mineturtle import *
from mcpi.mcpiblock import Block


def sphere(radius, material):
	material = Block.byName(str(material))
	turtle = Turtle()
	turtle.pendelay(0)
	# turtle.penup()
	turtle.penwidth(2*radius)
	turtle.penblock(material)
	turtle.go(0)
	turtle.pitch(90)
	turtle.penup()
	turtle.go(radius+2)