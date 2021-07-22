package com.multicraft;

import com.multicraft.data.BlockRecord;
import com.multicraft.data.BuildCommandData;
import com.multicraft.data.PreviousBuildsData;
import com.multicraft.data.Structures;
import com.multicraft.exceptions.NoCommandHistoryException;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashSet;


public class MultiCraftCommandExecutor implements CommandExecutor {
	private final MultiCraft plugin;
	private final String jarLocation;
	private final String MultiCraftDirName;
	private final CopyHandler copyHandler;

	public MultiCraftCommandExecutor(MultiCraft plugin) {
		this.plugin = plugin;
		File filePath = new File(MultiCraftCommandExecutor.class.
				getProtectionDomain().getCodeSource().getLocation().getPath());
		jarLocation = filePath.getPath().substring(0, filePath.getPath().indexOf(filePath.getName()));
		MultiCraftDirName = jarLocation + File.separator + "MultiCraft";
		copyHandler = new CopyHandler();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		String cmdName = cmd.getName().toLowerCase();

		switch (cmdName) {
			case "mundo":
				return Commands.undo(p, plugin);
			case "mredo":
				return Commands.redo(p, plugin);
			case "mbuild":
			case "mmbuild": {
				if (args.length < 2 || args.length > 5) {
					p.sendMessage("Incorrect number of arguments.");
					return false;
				}

				int[] dimensions;
				String materialArg;
				boolean isHollow = false;

				// /mbuild x y z material hollow [5 args]
				// /mbuild x y z material 		 [4 args]
				// /mbuild dim material hollow 	 [3 args]
				// /mbuild dim material 		 [2 args]
				if (args.length == 5 || args.length == 4) {
					dimensions = new int[]{Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])};
					materialArg = args[3];
					if (args.length == 5 && Integer.parseInt(args[4]) == 1) {
						isHollow = true;
					}
				} else {
					int dim = Integer.parseInt(args[0]);
					dimensions = new int[]{dim, dim, dim};
					materialArg = args[1];
					if (args.length == 3 && Integer.parseInt(args[2]) == 1) {
						isHollow = true;
					}
				}

				for (int d : dimensions) {
					if (d < 1) {
						p.sendMessage("You cannot have a zero or negative dimension.");
						return false;
					} else if (isHollow && d < 3) {
						p.sendMessage("You cannot have a hollow structure with those dimensions.");
						return false;
					}
				}

				Location startLocation;
				if (cmdName.equals("mbuild")) {
					startLocation = p.getLocation().getBlock().getLocation(); // rounds location coordinates
				} else {
					startLocation = p.getTargetBlock((HashSet<Byte>) null, 16).getLocation().add(0, 1, 0);
				}

				return Commands.build(p, p.getLocation(), startLocation, dimensions, materialArg, isHollow,
						p.getGameMode() == GameMode.SURVIVAL, plugin);
			}
			case "rbuild": {
				if (p.getGameMode() != GameMode.CREATIVE) {
					p.sendMessage("This command requires creative mode.");
					break;
				}
				RegionBuild rBuild = RegionBuild.getInstance();
				rBuild.startRegionBuildForPlayer(p);

				p.sendMessage("Please select the first position by pointing at it with a cursor and issuing the command"
						+ " /rloc1, then select the second position by pointing at it with the cursor and issuing command"
						+ " /rloc2. If the region is in the air, the part coordinates will be set as your position");
				return true;
			}
			case "rloc1": {
				if (p.getGameMode() != GameMode.CREATIVE) {
					p.sendMessage("This command requires creative mode.");
					break;
				}
				RegionBuild rBuild = RegionBuild.getInstance();
				Location startLocation = p.getTargetBlock((HashSet<Byte>) null, 16).getLocation().add(0, 1, 0);

				if (startLocation == null) startLocation = p.getLocation();
				rBuild.markStartPosition(p, startLocation);

				p.sendMessage("The first position has been marked.");
				return true;
			}
			case "rloc2": {
				if (p.getGameMode() != GameMode.CREATIVE) {
					p.sendMessage("This command requires creative mode.");
					break;
				}
				RegionBuild rBuild = RegionBuild.getInstance();
				Location endLocation = p.getTargetBlock((HashSet<Byte>) null, 16).getLocation().add(0, 1, 0);

				if (endLocation == null) endLocation = p.getLocation();

				if (!rBuild.markEndPosition(p, endLocation)) {
					p.sendMessage("You cannot mark an end location without beginning a valid region build session.");
					break;
				}

				p.sendMessage("The second position has been marked.");
				return true;
			}
			case "rrbuild": {
				if (p.getGameMode() != GameMode.CREATIVE) {
					p.sendMessage("This command requires creative mode.");
					break;
				}
				if (args.length > 1) {
					p.sendMessage("Incorrect number of arguments.");
					return false;
				}

				String materialArg;
				if (args.length == 1) {
					materialArg = args[0];
				} else {
					materialArg = "STONE";
					p.sendMessage("No material selected. Defaulting to stone.");
				}

				Material material;
				material = Material.getMaterial(materialArg.toUpperCase());
				if (material == null) {
					try {
						material = Material.getMaterial(Integer.parseInt(materialArg));
						if (material == null) {
							p.sendMessage("A material with that id does not exist.");
							return false;
						}
					} catch (NumberFormatException e) {
						p.sendMessage("A material with that name does not exist.");
						return false;
					}
				}

				RegionBuild rBuild = RegionBuild.getInstance();
				Location loc1 = rBuild.getStartLocation(p);
				Location loc2 = rBuild.getEndLocation(p);
				Commands.updateBlocks(loc1, loc2, material, plugin);

				p.sendMessage("Structure has been constructed in the region marked.");
				return true;
			}
			case "mstore": {
				if (args.length != 1) {
					p.sendMessage("Incorrect number of arguments.");
					break;
				}

				Structures universalStructures = new Structures(MultiCraftDirName +
						File.separator + "universal" + "StructureData.csv");
				Structures playerStructures = new Structures(MultiCraftDirName +
						File.separator + p.getUniqueId() + "StructureData.csv");

				BuildCommandData playerBuildData;
				try {
					playerBuildData = PreviousBuildsData.getInstance().getPlayersLastBuildRecord(p);
				} catch (NoCommandHistoryException e) {
					p.sendMessage("No previous builds found.");
					break;
				}

				BlockRecord start = playerBuildData.start;
				BlockRecord end = playerBuildData.end;
				int structureMaterial = p.getWorld().getBlockAt(start.x, start.y, start.z).getType().getId();
				int centerMaterial = p.getWorld().getBlockAt((start.x + end.x) / 2, (start.y + end.y) / 2,
						(start.z + end.z) / 2).getType().getId();
				int[] dimensions = playerBuildData.getDimensions();

				String[] entry;
				if (structureMaterial != centerMaterial) {
					entry = new String[]{args[0], Integer.toString(dimensions[0]), Integer.toString(dimensions[1]),
							Integer.toString(dimensions[2]), Integer.toString(structureMaterial), "1"};
				} else {
					entry = new String[]{args[0], Integer.toString(dimensions[0]), Integer.toString(dimensions[1]),
							Integer.toString(dimensions[2]), Integer.toString(structureMaterial), "0"};
				}

				boolean overwroteUniversal = universalStructures.setStructureData(entry);
				boolean overwrotePlayer = playerStructures.setStructureData(entry);

				p.sendMessage("Saved " + args[0] + ".");
				if (overwroteUniversal) p.sendMessage("Warning: Overwrote " + args[0] + " universally.");
				if (overwrotePlayer) p.sendMessage("Warning: Overwrote " + args[0] + " locally.");

				universalStructures.saveStructureData();
				playerStructures.saveStructureData();

				return true;
			}
			case "mclone": {
				if (args.length != 1) {
					p.sendMessage("Incorrect number of arguments.");
					break;
				}

				Structures universalStructures = new Structures(MultiCraftDirName +
						File.separator + "universal" + "StructureData.csv");
				Structures playerStructures = new Structures(MultiCraftDirName +
						File.separator + p.getUniqueId() + "StructureData.csv");

				// check for player-stored structure first, then universal
				String[] buildData = playerStructures.getStructureData(args[0]);
				if (buildData == null) {
					buildData = universalStructures.getStructureData(args[0]);
					if (buildData == null) {
						p.sendMessage(args[0] + " was not found.");
						break;
					}
				}

				String mmbuildArgs = String.join(" ", buildData);
				Bukkit.getPlayer(p.getUniqueId()).performCommand("mmbuild " + mmbuildArgs);
				p.sendMessage(args[0] + " was cloned.");
				return true;
			}
			case "copyloc1": {
				if (!p.isOp()) {
					p.sendMessage("This command requires op status.");
					break;
				}

				if (p.getGameMode() != GameMode.CREATIVE) {
					p.sendMessage("This command requires creative mode.");
					break;
				}

				Location startLocation = p.getTargetBlock((HashSet<Byte>) null, 32).getLocation();
				if (p.getWorld().getBlockAt(startLocation).getType() == Material.AIR) {
					p.sendMessage("Please find a closer position");
					break;
				}
				copyHandler.setCopyLoc1(p, startLocation);
				p.sendMessage("The first position has been marked.");
				return true;
			}
			case "copyloc2": {
				if (!p.isOp()) {
					p.sendMessage("This command requires op status.");
					break;
				}

				if (p.getGameMode() != GameMode.CREATIVE) {
					p.sendMessage("This command requires creative mode.");
					break;
				}

				Location endLocation = p.getTargetBlock((HashSet<Byte>) null, 32).getLocation();
				if (p.getWorld().getBlockAt(endLocation).getType() == Material.AIR) {
					p.sendMessage("Please find a closer position");
					break;
				}
				copyHandler.setCopyLoc2(p, endLocation);
				p.sendMessage("The second position has been marked.");
				return true;
			}
			case "mpaste": {
				if (!p.isOp()) {
					p.sendMessage("This command requires op status.");
					break;
				}

				if (p.getGameMode() != GameMode.CREATIVE) {
					p.sendMessage("This command requires creative mode.");
					break;
				}

				String copyArgs = copyHandler.getCopyArgs(p);
				if (copyArgs == null) {
					p.sendMessage("Please ensure both copy locations are marked.");
					break;
				}

				Location pasteLocation = p.getTargetBlock((HashSet<Byte>) null, 16).getLocation();
				if (p.getWorld().getBlockAt(pasteLocation).getType() == Material.AIR) {
					p.sendMessage("Please find a closer position");
					break;
				}

				String pasteArgs = pasteLocation.getX() + " " + (pasteLocation.getY() + 1) + " "
						+ pasteLocation.getZ() + " ";
				p.performCommand("clone " + copyArgs + pasteArgs + "masked");
				return true;
			}
			default:
				break;
		}
		p.sendMessage("No MultiCraft commands executed.");
		return false;
	}
}
