# MultiCraft
A project aimed at providing a voice and gaze interaction interface for Minecraft. This project is still under active development. To learn more about MultiCraft and its use, watch the playlist of videos [here](https://youtube.com/playlist?list=PL3vodYgfqF4uzGUH12sQxYU_WNLlLq23t).

## Development Setup
[py_setup.md](/docs/py_setup.md) has detailed information on setting up the Python side of the project to develop with (for [MultiCraftClient](https://github.com/tiilt-lab/MultiCraftClient/) and [MultiCraftTextServer](https://github.com/tiilt-lab/MultiCraftTextServer)). [java_setup.md](/docs/java_setup.md) contains information for setting up the Java development environment (for the MultiCraft plugin itself). Be sure to follow the instructions depending on what you are looking to work on.

## Gameplay Setup
### Setting up a MultiCraft Server
[MultiCraftServer](https://github.com/tiilt-lab/MultiCraft/tree/master/server) should provide enough information for creating your own Minecraft server with MultiCraft. In order for players and MultiCraftClients to access your server, you'll need to either have the server be set up on a computer with public IP address or use port forwarding on your router.
### Connecting to a MultiCraft Server in Minecraft
MultiCraft runs on Minecraft Java Edition version 1.14. You can download the Minecraft launcher [here](https://www.minecraft.net/en-us/download), create and launch a new 1.14 installation, and connect to the MultiCraft Minecraft server set up above.
### Connecting MultiCraftClients to a MultiCraft Server
[MultiCraftClient releases](https://github.com/tiilt-lab/MultiCraftClient/releases) should contain released versions of MultiCraftClients. Download the most recent version of `ClientGUI.exe` and follow the directions while running it to connect to the server.
