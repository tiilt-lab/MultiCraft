package com.multicraft;

import java.io.File;
import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class MultiCraftCommandExecutor implements CommandExecutor {
	private final MultiCraft plugin;
	private final String jarLocation;
	// private final String eyeTrackLocation;
	// private boolean eyeTracking;
	private final CopyHandler copyHandler;


	public MultiCraftCommandExecutor(MultiCraft plugin) {
		this.plugin = plugin;
		File filePath = new File(MultiCraftCommandExecutor.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		jarLocation = filePath.getPath().substring(0, filePath.getPath().indexOf(filePath.getName()));
		// eyeTrackLocation = jarLocation + "Tobii" + File.separator + "Interaction_Streams_101.exe";
		// eyeTracking = false;
		copyHandler = new CopyHandler();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		GameMode pGameMode = p.getGameMode();
		String cmdName = cmd.getName().toLowerCase();

		switch(cmdName) {
			case "mundo":
				return Commands.undo(p, plugin);
			case "mredo":
				return Commands.redo(p, plugin);
			case "mbuild":
			case "mmbuild": {
				if (args.length < 2 || args.length > 5) {
					p.sendMessage("Incorrect number of parameters.");
					return false;
				}

				int[] dimensions;
				int materialId;
				boolean isHollow = false;

				// /mbuild x y z material hollow [5 args]
				// /mbuild x y z material 		 [4 args]
				// /mbuild dim material hollow 	 [3 args]
				// /mbuild dim material 		 [2 args]
				if (args.length == 5 || args.length == 4) {
					dimensions = new int[]{Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])};
					materialId = Integer.parseInt(args[3]);
					if (args.length == 5 && Integer.parseInt(args[4]) == 1) {
						isHollow = true;
					}
				} else {
					int dim = Integer.parseInt(args[0]);
					dimensions = new int[]{dim, dim, dim};
					materialId = Integer.parseInt(args[1]);
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

				Material material = Material.getMaterial(materialId);
				Location startLocation;
				if (cmdName.equals("mbuild")) {
					startLocation = p.getLocation().getBlock().getLocation(); // rounds location coordinates
				} else {
					startLocation = p.getTargetBlock((HashSet<Byte>) null, 16).getLocation().add(0, 1, 0);
				}

				return Commands.build(p, p.getLocation(), startLocation, dimensions, material, isHollow,
						p.getGameMode() == GameMode.SURVIVAL, plugin);
			}
			case "rbuild": {
				RegionBuild rBuild = RegionBuild.getInstance();
				rBuild.startRegionBuildForPlayer(p);

				p.sendMessage("Please select the first position by pointing at it with a cursor and issuing the command"
						+ " /rloc1, then select the second position by pointing at it with the cursor and issuing command"
						+ " /rloc2. If the region is in the air, the part coordinates will be set as your position");
				return true;
			}
			case "rloc1": {
				RegionBuild rBuild = RegionBuild.getInstance();
				Location startLocation = p.getTargetBlock((HashSet<Byte>) null, 16).getLocation().add(0, 1, 0);

				if (startLocation == null) startLocation = p.getLocation();
				rBuild.markStartPosition(p, startLocation);

				p.sendMessage("The first position has been marked.");
				return true;
			}
			case "rloc2": {
				RegionBuild rBuild = RegionBuild.getInstance();
				Location endLocation = p.getTargetBlock((HashSet<Byte>) null, 16).getLocation().add(0, 1, 0);

				if (endLocation == null)
					endLocation = p.getLocation();

				if (!rBuild.markEndPosition(p, endLocation)) {
					p.sendMessage("You cannot mark an end location without beginning a valid region build session.");
					break;
				}

				p.sendMessage("The second position has been marked.");
				return true;
			}
			case "rrbuild": {
				RegionBuild rBuild = RegionBuild.getInstance();

				Location loc1 = rBuild.getStartLocation(p);
				Location loc2 = rBuild.getEndLocation(p);
				Commands.updateBlocks(loc1, loc2, Material.getMaterial(1), plugin);

				p.sendMessage("Structure has been constructed in the region marked.");
				return true;
			}
			case "eyebuild": {
				// if (args.length < 3) {
				// 	p.sendMessage("Not enough parameters.");
				// 	break;
				// }

				// new BukkitRunnable() {
				// 	@Override
				// 	public void run() {
				// 		Runtime run = Runtime.getRuntime();
				// 		String[] eyeTrackCommand = {eyeTrackLocation, "-d"};
				// 		try {
				// 			p.sendMessage("Tracking eyes...");
				// 			Process eyeTrack = run.exec(eyeTrackCommand);

				// 			while (eyeTrack.isAlive()) { /* Wait for eye tracking executable to complete. */ }

				// 			Bukkit.getScheduler().runTask(plugin, () -> {
				// 				p.sendMessage("Building Structure...");
				// 				String mmbuild_args = String.join(" ", args);
				// 				Bukkit.getPlayer(p.getUniqueId()).performCommand("mmbuild " + mmbuild_args);
				// 			});

				// 		} catch (Exception e) {
				// 			e.printStackTrace();
				// 		}
				// 	}
				// }.runTaskAsynchronously(this.plugin);

				p.sendMessage("Please use Multicraft client to initiate eye tracking commands.");

				return true;
			}
			case "eyetrack": {
				// if (!eyeTracking) {
				// 	new BukkitRunnable() {
				// 		@Override
				// 		public void run() {
				// 			Runtime run = Runtime.getRuntime();
				// 			Process eyeTrack;
				// 			String eyeTrackCommand = eyeTrackLocation;
				// 			if(args.length == 1 && args[0].equalsIgnoreCase("move"))
				// 				eyeTrackCommand += " -m";

				// 			try {
				// 				p.sendMessage("Tracking eyes, press . to end.");
				// 				eyeTrack = run.exec(eyeTrackCommand);
				// 				eyeTracking = true;

				// 				while (eyeTrack.isAlive()) { /* Wait for eye tracking executable to complete. */ }

				// 				p.sendMessage("No longer tracking eyes.");
				// 				eyeTracking = false;

				// 			} catch (Exception e) {
				// 				e.printStackTrace();
				// 			}
				// 		}
				// 	}.runTaskAsynchronously(this.plugin);
				// }

				p.sendMessage("Please use Multicraft client to initiate eye tracking commands.");

				return true;
			}
			case "mstore": {
				if(args.length != 1) {
					p.sendMessage("Improper number of parameters.");
					break;
				}

				StructureData universalStructureData = new StructureData(jarLocation + "\\MultiCraft\\" + "universal" + "StructureData.csv");
				StructureData playerStructureData = new StructureData(jarLocation + "\\MultiCraft\\" + p.getUniqueId() + "StructureData.csv");

				BuildCommandData playerBuildData;
				try { playerBuildData = PreviousBuildsData.getInstance().getPlayersLastBuildRecord(p); }
				catch(NoCommandHistoryException e) {
					p.sendMessage("No previous builds found.");
					break;
				}

				BlockRecord start = playerBuildData.start;
				BlockRecord end = playerBuildData.end;
				int structureMaterial = p.getWorld().getBlockAt(start.x, start.y, start.z).getType().getId();
				int centerMaterial = p.getWorld().getBlockAt((end.x - start.x) / 2, (end.y - start.y) / 2, (end.z - start.z) / 2).getType().getId();
				int[] dimensions = playerBuildData.getDimensions();

				String[] entry;
				if (structureMaterial != centerMaterial)
					entry = new String[]{args[0],
							Integer.toString(dimensions[0]),
							Integer.toString(dimensions[1]),
							Integer.toString(dimensions[2]),
							Integer.toString(structureMaterial),
							"hollow"};
				else
					entry = new String[] { args[0],
							Integer.toString(dimensions[0]),
							Integer.toString(dimensions[1]),
							Integer.toString(dimensions[2]),
							Integer.toString(structureMaterial)};

				boolean overwroteUniversal = universalStructureData.setStructureData(entry);
				boolean overwrotePlayer = playerStructureData.setStructureData(entry);

				p.sendMessage("Saved " + args[0] + ".");
				if(overwroteUniversal) p.sendMessage("Warning: Overwrote " + args[0] + " universally.");
				if(overwrotePlayer) p.sendMessage("Warning: Overwrote " + args[0] + " locally.");

				universalStructureData.saveStructureData();
				playerStructureData.saveStructureData();

				return true;
			}
			case "mclone": {
				if(args.length != 1) {
					p.sendMessage("Improper number of parameters");
					break;
				}

				StructureData universalStructureData = new StructureData(jarLocation + "\\MultiCraft\\" + "universal" + "StructureData.csv");
				StructureData playerStructureData = new StructureData(jarLocation + "\\MultiCraft\\" + p.getUniqueId() + "StructureData.csv");

				// check for player-stored structure first, then universal
				String[] buildData = playerStructureData.getStructureData(args[0]);
				if(buildData == null) {
					buildData = universalStructureData.getStructureData(args[0]);
					if(buildData == null) {
						p.sendMessage(args[0] + " was not found.");
						break;
					}
				}

				String mmbuild_args = String.join(" ", buildData);
				Bukkit.getPlayer(p.getUniqueId()).performCommand("mmbuild " + mmbuild_args);
				p.sendMessage(args[0] + " was cloned.");
				return true;
			}
			case "copyloc1": {
				if(!p.isOp()) {
					p.sendMessage("This command requires op status.");
					break;
				}

				Location startLocation = p.getTargetBlock((HashSet<Byte>) null, 32).getLocation();
				if(p.getWorld().getBlockAt(startLocation).getType() == Material.AIR) {
					p.sendMessage("Please find a closer position");
					break;
				}
				copyHandler.setCopyLoc1(p, startLocation);
				p.sendMessage("The first position has been marked.");
				return true;
			}
			case "copyloc2": {
				if(!p.isOp()) {
					p.sendMessage("This command requires op status.");
					break;
				}

				Location endLocation = p.getTargetBlock((HashSet<Byte>) null, 32).getLocation();
				if(p.getWorld().getBlockAt(endLocation).getType() == Material.AIR) {
					p.sendMessage("Please find a closer position");
					break;
				}
				copyHandler.setCopyLoc2(p, endLocation);
				p.sendMessage("The second position has been marked.");
				return true;
			}
			case "mpaste": {
				if(!p.isOp()) {
					p.sendMessage("This command requires op status.");
					break;
				}

				String copyArgs = copyHandler.getCopyArgs(p);
				if(copyArgs == null) {
					p.sendMessage("Please ensure both copy locations are marked.");
					break;
				}

				Location pasteLocation = p.getTargetBlock((HashSet<Byte>) null, 16).getLocation();
				if(p.getWorld().getBlockAt(pasteLocation).getType() == Material.AIR) {
					p.sendMessage("Please find a closer position");
					break;
				}

				String pasteArgs = pasteLocation.getX() + " " + (pasteLocation.getY() + 1) + " " + pasteLocation.getZ() + " ";
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