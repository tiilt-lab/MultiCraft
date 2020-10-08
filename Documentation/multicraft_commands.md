# Commands in MultiCraft
These are commands that have been added to Minecraft via the MultiCraft plugin in the server. An asterisk (*)indicates that these commands are accessible via voice commands, and a caret (^) indicates that these commands require a Tobii eye tracker to be set up on the machine to run.

## /mbuild
__Inputs:__ `x` `y` `z` `material` `hollow`

__Description__: Build an `x` by `y` by `z` structure of  `material` at the player's current position. The structure may also be made hollow if `hollow` == 1

## /mmbuild (*)
__Inputs:__ `x` `y` `z` `material` `hollow`

__Description__: Build an `x` by `y` by `z` structure of  `material` at the player's mouse position. The structure may also be made hollow if `hollow` == 1

## /rbuild
__Inputs:__ None

__Description:__ Initiates region building for the `rrbuild` command. This command must be called first if region building is to be used

## /rloc1
__Inputs:__ None, uses the player's mouse position

__Description:__ Marks the first location for the `rrbuild` command. This command must be called second in the region building command sequence

## /rloc2
__Inputs:__ None, uses the player's mouse position

__Description:__ Marks the second location for the `rrbuild` command. This command must be called third in the region building command sequence

## /rrbuild
__Inputs:__ None

__Description:__ Fills the space between the locations marked by the `rloc1` and `rloc2` commands with stone. This command must be called last in the region building command sequence

## /eyebuild (*^)
__Inputs:__ `x` `y` `z` `material` `hollow`

__Description:__ Build an `x` by `y` by `z` structure of  `material` at the player's cursor position after using the eye tracker to find a spot to build. The structure may also be made hollow if `hollow` == 1

## /eyetrack (*^)
__Inputs:__ `move`

__Description:__ Enables eye tracking. If the `move` keyword is given, the player can also move forward if they stare in that direction for enough time

## /msave (*)
__Inputs:__ `name`

__Description:__ Save the most recent structure created using a build command and name it `name`

## /mclone (*)
__Inputs:__ `name`

__Description:__ Clone a saved structure named `name`

## /mundo (*)
__Inputs:__ None

__Description:__ Undo the most recent strucrure created using a build command (mbuild, mmbuild, eyebuild, redo)

## /mredo (*)
__Inputs:__ None

__Description:__ Redo the most recent structure removed using the undo command

## /copyloc1
__Inputs:__ None, uses the player's mouse position

__Description:__ Marks the first location for the selection of the copy tool

## /copyloc2
__Inputs:__ None, uses the player's mouse position

__Description:__ Marks the second location for the selection of the copy tool

## /mpaste
__Inputs:__ None, uses the player's mouse position

__Description:__ Copies the region defined by `copyloc1` and `copyloc2`to cursor position (uses Minecraft's `clone` command, therefore requires OP priveleges)

Some commands require OP privileges. In order to ensure you can use all commands, please enter your Minecraft UUID, Minecraft username, and level "4" in the `MultiCraftServer\ops.json` file. Modify the entry already in the file to fit your information.
