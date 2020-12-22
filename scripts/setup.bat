@echo off
echo Please set up and initialize Miniconda to use this program. See manual_setup.md for more details. Use Ctrl+C to cancel.
timeout 10
call conda create --name tiilt-multi python=3.6
call conda activate tiilt-multi
call conda install -c anaconda pyaudio
call pip install ibm_watson
call pip install spacy
call python -m spacy download en_core_web_sm
call pip install nltk
call python -m nltk.downloader wordnet
call pip install word2number
pause