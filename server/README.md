# MultiCraftServer
This repo contains instructions for creating your own Minecraft server with MultiCraft. Running a Minecraft server requires Java, which can be downloaded here: https://www.java.com/en/download/

## Instructions
1. Clone or download the ZIP of this repo onto the machine which will host the server.
    * If downloaded, unzip and move folder to proper location.
2. Open the folder in your terminal and run `java -jar BuildTools.jar --rev 1.14`
3. Wait for the setup to complete (this may take some time).
4. Double-click `run.bat` to generate server files.
    * Mac: run `./start.sh` in terminal
6. Find the new `eula.txt` file in the folder and change `eula=false` to `eula=true`.
7. Add `MultiCraft.jar` to the newly created `plugins` folder (you may need to use `run.bat` again if the folder hasn't appeared yet).
    * `MultiCraft.jar` may be found here: https://github.com/mendozatudares/MultiCraftServer/releases/
8. Double-click `run.bat` to start the server.
