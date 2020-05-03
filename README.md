# tiiltMultiCraft
A project aimed at providing a voice and gaze interaction interface for Minecraft. This project is still under active development.

## Gameplay Setup
Please read [manual_setup.md](manual_setup.md) for more detailed information. This project works best if you install Miniconda3 (Miniconda for Python 3.X) to run the Python side. You can click here for the [Windows Installation](https://repo.anaconda.com/miniconda/Miniconda3-latest-Windows-x86_64.exe), or here for the rest: https://docs.conda.io/en/latest/miniconda.html. Once you install Miniconda3, be sure to initialize it to run in in your terminal or use the Anaconda Prompt. Currently, there are only automated setup and start scripts for Windows (`setup.bat` and `start.bat`).

## Development Setup
[manual_setup.md](manual_setup.md) has detailed information on setting up the Python side of the project to develop with. Be sure to follow the instructions there. The instructions below are for setting up the Java side of the project.

### IntelliJ
#### Development Environemnt Setup
1. Clone repository.
2. Open folder using IntelliJ.
3. Ensure Project SDK is 1.8 (File > Project Structure > Project SDK).
4. Project should build without errors.

#### Exporting jar plugin file
1. Open Project Artifacts (File > Project Structure > Artifacts).
2. Add a new artifact (Add > JAR > From modules with dependencies...).
3. Under Output Layout, add the plugin.yml file (Add > File > plugin.yml).
4. Output layout should contain craftbukkit.jar and 'json-simple.jar' along with  'MultiCraft' compile output and plugin.yml.
5. Change output directory to plugins folder in the folder where you are running your sever.
6. Ensure Main Class is com.multicraft.MultiCraft.
7. Click Apply and OK.
8. Build jar file (Build > Build Artifacts... > MultiCraft.jar > Build).
9. You should find MultiCraft.jar file in your server's plugins folder.

### Eclipse
#### Development Environment Setup
1. Create a new Java Project with the name MultiCraft
2. Set JRE to 1.8
3. Replace the src folder in the project folder with the src folder from the repository
4. Go to ‘Configure Build Path -> Add external jars’ and add the craftbukkit jar file from the requiredFiles folder.
5. Copy the plugin yml file to the project folder on the same level as the src folder.
6. Refresh the project and everything should work fine. 

#### Exporting jar plugin file
1. Go to export -> jar file.
2. Uncheck everything on the right other than the plugin.yml file.
3. Change the export destination to your server's plugins folder.
4. Finish.

# Setting up a Minecraft Server
Visit https://bukkit.gamepedia.com/Setting_up_a_server for detailed instructions.


