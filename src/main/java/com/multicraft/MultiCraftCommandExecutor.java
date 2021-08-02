package com.multicraft;

import com.multicraft.data.BlockRecord;
import com.multicraft.data.BuildCommandRecord;
import com.multicraft.data.PreviousBuildRecords;
import com.multicraft.data.StructureMap;
import com.multicraft.exceptions.NoCommandHistoryException;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;


public class MultiCraftCommandExecutor implements CommandExecutor {

	private final MultiCraft plugin;

	public MultiCraftCommandExecutor(MultiCraft plugin) {
		this.plugin = plugin;
	}
	
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
					break;
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

				Location startLocation = cmdName.equals("mbuild") ? p.getLocation() : Commands.getPlayerTargetLocation(p, 16, true);

				return Commands.build(p, p.getLocation(), startLocation, dimensions, materialArg, isHollow,
						p.getGameMode() == GameMode.SURVIVAL, plugin);
			}
			case "rloc1": {
				if (playerNotInCreativeMode(p)) break;

				Location startLocation = Commands.getPlayerTargetLocation(p, 16, true);

				RegionSelector.getInstance().markStartPosition(p, startLocation);

				p.sendMessage("The first position has been marked.");
				return true;
			}
			case "rloc2": {
				if (playerNotInCreativeMode(p)) break;

				Location endLocation = Commands.getPlayerTargetLocation(p, 16, true);

				if (!RegionSelector.getInstance().markEndPosition(p, endLocation)) {
					p.sendMessage("You cannot mark an end location without beginning a valid region session.");
					break;
				}

				p.sendMessage("The second position has been marked.");
				return true;
			}
			case "rbuild": {
				if (args.length > 1) {
					p.sendMessage("Incorrect number of arguments.");
					break;
				}

				if (playerNotInCreativeMode(p)) break;

				Material material = Commands.getMaterial(args.length == 1 ? args[0] : "STONE");

				Location loc1 = RegionSelector.getInstance().getStartLocation(p);
				Location loc2 = RegionSelector.getInstance().getEndLocation(p);

				List<BlockRecord> blocksAffected  = Commands.updateBlocks(loc1, loc2, material, plugin);
				Commands.updateUndoAndRedoStacks(blocksAffected, p);

				p.sendMessage("Structure has been constructed in the region marked.");
				return true;
			}
			case "mstore": {
				if (args.length != 1) {
					p.sendMessage("Incorrect number of arguments.");
					break;
				}

				StructureMap universalStructureMap = new StructureMap(plugin.MultiCraftDirName + File.separator + "universal" + "StructureData.csv");
				StructureMap playerStructureMap = new StructureMap(plugin.MultiCraftDirName + File.separator + p.getUniqueId() + "StructureData.csv");

				BuildCommandRecord playerBuildData;
				try {
					playerBuildData = PreviousBuildRecords.getInstance().getPlayersLastBuildRecord(p);
				} catch (NoCommandHistoryException e) {
					p.sendMessage("No previous builds found.");
					break;
				}

				BlockRecord start = playerBuildData.start;
				BlockRecord end = playerBuildData.end;
				Material structureMaterial = p.getWorld().getBlockAt(start.x, start.y, start.z).getType();
				Material centerMaterial = p.getWorld().getBlockAt((start.x + end.x) / 2, (start.y + end.y) / 2, (start.z + end.z) / 2).getType();
				int[] dimensions = playerBuildData.getDimensions();

				String isHollow = structureMaterial != centerMaterial ? "1" : "0";
				String[] entry = new String[]{args[0], Integer.toString(dimensions[0]), Integer.toString(dimensions[1]), Integer.toString(dimensions[2]), structureMaterial.toString(), isHollow};

				boolean overwroteUniversal = universalStructureMap.setStructureData(entry);
				boolean overwrotePlayer = playerStructureMap.setStructureData(entry);

				p.sendMessage("Saved " + args[0] + ".");
				if (overwroteUniversal) p.sendMessage("Warning: Overwrote " + args[0] + " universally.");
				if (overwrotePlayer) p.sendMessage("Warning: Overwrote " + args[0] + " locally.");

				universalStructureMap.saveStructureData();
				playerStructureMap.saveStructureData();

				return true;
			}
			case "mclone": {
				if (args.length != 1) {
					p.sendMessage("Incorrect number of arguments.");
					break;
				}

				StructureMap universalStructureMap = new StructureMap(plugin.MultiCraftDirName + File.separator + "universal" + "StructureData.csv");
				StructureMap playerStructureMap = new StructureMap(plugin.MultiCraftDirName + File.separator + p.getUniqueId() + "StructureData.csv");

				// check for player-stored structure first, then universal
				String[] buildData = playerStructureMap.getStructureData(args[0]);
				if (buildData == null) {
					buildData = universalStructureMap.getStructureData(args[0]);
					if (buildData == null) {
						p.sendMessage(args[0] + " was not found.");
						break;
					}
				}

				p.sendMessage(" Cloning " + args[0] + ".");
				String mmbuildArgs = String.join(" ", buildData);
				return p.performCommand("mmbuild " + mmbuildArgs);
			}
			case "copyloc1": {
				if (playerNotOp(p) || playerNotInCreativeMode(p)) {
					break;
				}

				Location startLocation = Commands.getPlayerTargetLocation(p, 16, true);
				CopyHandler.getInstance().markStartPosition(p, startLocation);

				p.sendMessage("The first position has been marked.");
				return true;
			}
			case "copyloc2": {
				if (playerNotOp(p) || playerNotInCreativeMode(p)) {
					break;
				}

				Location endLocation = Commands.getPlayerTargetLocation(p, 16, true);
				if (!CopyHandler.getInstance().markEndPosition(p, endLocation)) {
					p.sendMessage("You cannot mark an end location without beginning a valid region session.");
					break;
				}

				p.sendMessage("The second position has been marked.");
				return true;
			}
			case "mpaste": {
				String copyArgs = CopyHandler.getInstance().getCopyArgs(p);
				if (playerNotOp(p) || playerNotInCreativeMode(p) || copyArgs.isEmpty()) {
					break;
				}

				Location pasteLocation = Commands.getPlayerTargetLocation(p, 16, true);
				String pasteArgs = pasteLocation.getX() + " " + pasteLocation.getY() + " " + pasteLocation.getZ() + " ";

				return p.performCommand("clone " + copyArgs + pasteArgs + "masked");
			}
			default:
				break;
		}
		p.sendMessage("No MultiCraft commands executed.");
		return false;
	}

	private static boolean playerNotOp(Player p) {
		if (!p.isOp()) {
			p.sendMessage("You must be OP to use this command.");
			return true;
		}
		return false;
	}

	private static boolean playerNotInCreativeMode(Player p) {
		if (p.getGameMode() != GameMode.CREATIVE) {
		    p.sendMessage("You must be in Creative Mode to use this command.");
			return true;
		}
		return false;
	}

}
