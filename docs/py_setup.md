# MultiCraft Python Setup Instructions
## MultiCraftClient
Client for executing natural language commands within MultiCraft

### Development Setup
These are instructions for setting up MultiCraftClient on your system for development. Note that the instructions below have been run in Windows 10 cmd.
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
pip install -r requirements.txt
```

#### Startup
After your Minecraft server with MultiCraft and TextServer are running, and the variables for `API_KEY`, `SERVICE_URL`, `CUSTOMIZATION_ID`, and endpoint for the TextServer function in your client are correct, you can start the client using the commands below.
```
conda activate tiilt-multi
python ClientGUI.py
```

#### Issues
If the `pip install -r requirements.txt` command fails when installing the Client modules, this may be due to PyAudio requiring PortAudio (which likely isn't installed on Windows). Try using `conda install -c anaconda pyaudio` and try again.

### Gameplay Setup
#### Connecting MultiCraftClients to a MultiCraft Server
[MultiCraftClient releases](https://github.com/mendozatudares/MultiCraftClient/releases) should contain released versions of MultiCraftClients. Download the most recent version of `Client.exe` and follow the directions while running it to connect to the server.

## MultiCraftTextServer
Serverless function deployed to Azure, tasked with parsing natural language commands received from MultiCraft clients

### Development Setup
These are instructions for setting up MultiCraftTextServer on your system for development. Note that the instructions below have been run in Bash on Debian 10 (buster).
The TextServer has been set up to deploy to Microsoft Azure Functions. Please see these guides for using either [VS Code](https://docs.microsoft.com/en-us/azure/azure-functions/create-first-function-vs-code-python#configure-your-environment) or the [Command Line](https://docs.microsoft.com/en-us/azure/azure-functions/create-first-function-cli-python?tabs=azure-cli%2Cbash%2Cbrowser#configure-your-local-environment) for configuring your environment.

Setup a new python virtual environment
```
python3 -m venv .venv
source .venv/bin/activate
pip3 install -r requirements.txt
```
Use the Azure Functions Core Tools for initiating the function using `func init`. Be sure to choose python for the worker runtime.

See the [MultiCraftServer repo](https://github.com/mendozatudares/MultiCraftServer/) for information about setting up a Minecraft server with MultiCraft running on it.

### Startup
The TextServer may run locally using `func start` in the MultiCraftTextServer directory or by pressing `F5` in VS Code. The endpoint that the function will be running at should be of the form `https://localhost:XXXX/httptrigger1`.
```
source .venv/bin/activate
func start
```
You can then test the function by accessing the endpoint using your browser or a [MultiCraftClient](https://github.com/mendozatudares/MultiCraftClient/releases/). Be sure to have a Minecraft server with MultiCraft to ensure commands go through.
