# MultiCraft Manual Setup Instructions
These are instructions for running and playing MultiCraft on your system. Note that MultiCraft runs on Minecraft Java version 1.9, and the instructions below have been run in Windows 10 Command Prompt.
__NOTE:__ These instructions assume you already have anaconda or miniconda installed. If you do not have it installed, you can install it at the links below:

__MINICONDA3 FOR WINDOWS__: https://repo.anaconda.com/miniconda/Miniconda3-latest-Windows-x86_64.exe

After installing anaconda and opening the Miniconda3 Prompt, follow the instructions. These commands are also automated in ```setup.bat```

Setup a new conda environment with python
```
conda create --name tiilt-multi python=3.6
conda activate tiilt-multi
```

Install python modules for Client
```
conda install -c anaconda pyaudio
pip install ibm_watson
```

Install python modules for Server
```
pip install spacy
python -m spacy download en_core_web_sm
pip install nltk
pip install word2number
```
To run the MultiCraft Server, look in the MultiCraftServer folder and run the ```run.bat``` file.
In order to use voice commands, run the following commands in two different  the root tiiltMultiCraft folder. Startup can also be automated through the ```start.bat``` file in the root tiiltMultiCraft folder.

Wait for the MultiCraft Server to start, then start the Speech Server
```
conda activate tiilt-multi
python Server\ClientStringsServer.py
```
Wait for the Speech Server to connect, then start the Speech Client in a new window
```
conda activate tiilt-multi
python Client\MultiCraftClientAudioHandler.py
```
After the Speech Client connects, you can then enter your Minecraft Java __Full__ UUID when prompted (enter your Minecraft username here if you don't know: https://mcuuid.net/).