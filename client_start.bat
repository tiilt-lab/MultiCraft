@echo off
call conda activate base
call conda activate tiilt-multi
start /d "Client" call python ClientGUI.py
echo Connected to Multicraft client.