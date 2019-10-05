import re
import time
import pyautogui


def process_command(input_s):
    input_s = input_s.lower()
    process_s = ''
    
    if 'move' in input_s:
        coord = read_coord_string(input_s.split(' ', 1)[1])
        process_s = move_mouse(coord)
        
    return process_s


def move_mouse(coord):
    pyautogui.move(coord[0], coord[1], 1, pyautogui.easeInOutQuad)
    return 'moving... X: ' + str(coord[0]).ljust(4) + ' Y: ' + str(coord[1]).ljust(4)


def read_coord_string(coord_string):
    coord_string = re.sub('[^0-9,\s-]', '', coord_string)
    
    if ',' in coord_string:
        coord_list = coord_string.split(', ')
    else:
        coord_list = coord_string.split(' ')
    
    return (int(coord_list[0]), int(coord_list[1]))


if __name__ == "__main__":
    print("now accepting commands, enter stop to quit...")
        
    while True:
        input_s = input("please enter a movement to simulate: ")
        if input_s == 'stop':
            break
    
        print(process_command(input_s))
        time.sleep(1)
    
    print("no longer accepting commands.\n")