@echo off
echo Please wait for all three processes to start to continue.
start /d "MultiCraftServer" call run.bat
timeout 60
call conda activate base
call conda activate tiilt-multi
start "" call python Server\ClientStringsServer.py
timeout 30
start /d "Client" call python MultiCraftClientAudioHandler.py
echo Multicraft has started! You may now join the server on Minecraft 1.9 (Direct Connect to Server Address: localhost).