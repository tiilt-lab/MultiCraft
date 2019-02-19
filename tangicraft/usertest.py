#
# Code by Alexander Pruss and under the MIT license
#

from mineturtle import *

t = Turtle()
t.turtle(GIANT)
t.pendelay(0.01)
t.angle(0) # align to grid

x = t.position.x
y = t.position.y
z = t.position.z

steps = 30
wall_Height = 5
yard_num = 30

def build():
	t.penblock(block.STONE)
	t.pendown()
	t.roll(-90)
	t.go(steps)
	t.pitch(90)
	t.go(steps)
	t.pitch(90)
	t.go(steps)
	t.pitch(90)
	t.go(steps)
	t.yaw(90)
	t.penup()
	t.go(1)
	t.penblock(block.GLASS)
	t.pendown()
	t.yaw(-90)
	t.angle(0) 
	 
def move():
	t.penup()
	t.angle(0)
	t.go(steps)
	
	
def ground():
	t.pitch(-90)
	t.go(wall_Height)
	


	 
#testing
t.goto(x,y,z)
steps2 = steps
for i in range(yard_num):
	for i in range(wall_Height):
		build()
	x = x+steps
	t.goto(x,y,z)

	
	
	

 
#  face()
#  t.roll(-90)
#  face()
#  t.roll(90)
#  t.pitch(90)
#  face()
#  t.pitch(-90)
#  t.penup()
#  t.go(20)
#  t.yaw(90)
#  t.go(20)
#  t.pitch(90)
#  t.go(20)
#  t.pitch(-90)
#  t.yaw(90)
#  t.pitch(-90)
#  t.pendown()

