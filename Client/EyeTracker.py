import os
import platform
import subprocess


def process_eye_tracking(transcript):
    tokens = transcript.split()
    supported_commands = ['build', 'place', 'move', 'track', 'turn', 'tilt', 'undo', 'redo', 'store', 'clone', 'give']
    comm = ''
    for index, word in enumerate(tokens):
        if word in supported_commands: 
            comm = word
            break
    
    if comm == 'track':
        # activate eye tracking prior to sending command
        if platform.system() == 'Windows':
            file_path = os.path.dirname(os.path.abspath(__file__))
            command = [os.path.join(file_path, 'Tobii', 'Interaction_Streams_101.exe')]
            
            if 'move' in tokens:
                command.append('-m')
            elif 'build' in tokens:
                command.append('-d')

            print('Eye tracking, press . to stop...')
            subprocess.run(command)
        else:
            print('Eye tracking requries Windows.')
            

