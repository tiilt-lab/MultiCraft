# MultiCraft Python Setup Instructions
These are instructions for setting up MultiCraft on your system for development. Note that MultiCraft runs on Minecraft Java version 1.9, and the instructions below have been run in Windows 10 cmd.
__NOTE:__ These instructions assume you already have anaconda or miniconda installed. If you do not have it installed, you can install it at the links below:

__MINICONDA3 FOR WINDOWS__: https://repo.anaconda.com/miniconda/Miniconda3-latest-Windows-x86_64.exe

__OTHER MINICONDA3 INSTALLATIONS__: https://docs.conda.io/en/latest/miniconda.html

After installing anaconda and opening the Anaconda Prompt (Miniconda3), or after installing and initializing your own prompt, follow the instructions.

Setup a new conda environment with python
```
conda create --name tiilt-multi python=3.6
```

Install python modules for Client Development
```
conda activate tiilt-multi
cd MultiCraftClient
pip install -r requirements.txt
```

or install python modules for TextServer Development
```
conda activate tiilt-multi
cd MultiCraftTextServer
pip install -r requirements.txt
```
The TextServer has been set up to deploy to Microsoft Azure Functions. Please use these guides for using either [VS Code](https://docs.microsoft.com/en-us/azure/azure-functions/create-first-function-vs-code-python) or the [Command Line](https://docs.microsoft.com/en-us/azure/azure-functions/create-first-function-cli-python?tabs=azure-cli%2Cbash%2Cbrowser) to set up your environment further.

See the [MultiCraftTextServer repo](https://github.com/mendozatudares/MultiCraftServer/) for information about setting up a Minecraft server with MultiCraft running on it.

## Startup
The TextServer may run locally using `func start` in the MultiCraftTextServer directory or by pressing `F5` in VS Code. The endpoint that the function will be running at should be of the form `https://localhost:XXXX/httptrigger1`.
```
conda activate tiilt-multi
cd MultiCraftTextServer
func start
```
After your Minecraft server with MultiCraft and TextServer are running, and the variables for `API_KEY`, `SERVICE_URL`, `CUSTOMIZATION_ID`, and endpoint for the TextServer function in you client are correct, you can start the client using the commands below.
```
conda activate tiilt-multi
cd MutliCraftClient
python ClientGUI.py
```

## Issues
If the `pip install -r requirements.txt` command fails when installing the Client modules, this may be due to PyAudio requiring PortAudio (which likely isn't installed on Windows). Try using `conda install -c anaconda pyaudio` and try again.