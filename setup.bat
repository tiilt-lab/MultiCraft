@echo off
echo Please set up Miniconda to use this program. See manual_setup.md for more details. Use Ctrl+C to cancel.
timeout 10
call conda create --name tiilt-multi-copy python=3.6
call conda activate tiilt-multi-copy
call conda install -c anaconda pyaudio
call pip install ibm_watson
call pip install spacy
call python -m spacy download en_core_web_sm
call pip install nltk
call pip install word2number
pause