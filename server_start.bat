@echo off
echo Please wait...
start /d "MultiCraftServer" call run.bat
timeout 60
call conda activate base
call conda activate tiilt-multi
start /d "Server" call python TextServer.py
echo Multicraft has started.