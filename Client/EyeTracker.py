import os
import platform
import subprocess


def process_eye_tracking(transcript):
    tokens = transcript.split('\s')
    supported_commands = ['build', 'place', 'move', 'track', 'turn', 'tilt', 'undo', 'redo', 'store', 'clone', 'give']
    comm = 0
    for index, word in enumerate(tokens):
        if word in supported_commands: 
            comm = word
            break
    
    if comm == 'track':
        # activate eye tracking prior to sending command
        if platform.system() == 'Windows':
            file_path = os.path.abspath(__file__)
            command = [os.path.join(file_path, 'Tobii', 'Interaction_Streams_101.exe')]
            
            if 'move' in tokens:
                command.append('-m')

            subprocess.run(command)
        else:
            print('Eyetracking requries Windows.')

    return transcript
            

