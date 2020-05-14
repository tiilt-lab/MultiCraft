@echo off
call conda activate base
call conda activate tiilt-multi
start /d "Client" call python SpeechHandler.py
echo Connected to Multicraft client.