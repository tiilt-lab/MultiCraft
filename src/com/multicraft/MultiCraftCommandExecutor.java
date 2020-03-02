package com.multicraft;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import com.multicraft.Materials.MaterialDoesNotExistException;


public class MultiCraftCommandExecutor implements CommandExecutor{
	private final MultiCraft plugin;
	private String jarLocation;
	public StructureData structureData;
	private boolean eyeTracking;
	private String eyeTrackExecutable;
	private BukkitTask eyeTrackRunnable;

	
	public MultiCraftCommandExecutor(MultiCraft plugin) {
		this.plugin = plugin;
		File filePath = new File(MultiCraftCommandExecutor.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		jarLocation = filePath.getPath().substring(0, filePath.getPath().indexOf(filePath.getName()));
		eyeTracking = false;
		eyeTrackExecutable = "Tobii" + File.separator + "Interaction_Streams_101.exe";
		structureData = new StructureData(jarLocation + "StructureData.csv");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		String cmdName = cmd.getName().toLowerCase();

		switch(cmdName) {
			case "mundo":
				return Commands.undo(p, this.plugin);
			case "mredo":
				return Commands.redo(p, this.plugin);
			case "mbuild": {
				if (args.length < 3) {
					p.sendMessage("Not enough parameters.");
					break;
				}

				int[] dimensions = new int[]{Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])};
				Location startLocation = p.getLocation();

				int materialId = 1;
				if (args.length > 3)
					try { materialId = Integer.parseInt(args[3]); } catch (NumberFormatException e) {
						try { materialId = Materials.getId(args[3]); } catch (MaterialDoesNotExistException f) {
							p.sendMessage("The material you specified does not exists. Defaulting to stone.");
							materialId = 1;
						}
					}

				Material material = Material.getMaterial(materialId);
				List<BlockRecord> blocksAffected = Commands.buildStructure(startLocation, dimensions, material, args.length > 4);
				Commands.updateUndoAndRedoStacks(blocksAffected, p);

				return true;
			}
			case "mmbuild": {
				if (args.length < 3){
					p.sendMessage("Not enough parameters.");
					break;
				}

				int[] dimensions = new int[]{Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])};
				Location startLocation = p.getTargetBlock((HashSet<Byte>) null, 16).getLocation().add(0, 1, 0);

				int materialId = 1;
				if (args.length > 3)
					try { materialId = Integer.parseInt(args[3]); } catch (NumberFormatException e) {
						try { materialId = Materials.getId(args[3]); } catch (MaterialDoesNotExistException f) {
							p.sendMessage("The material you specified does not exists. Defaulting to stone.");
							materialId = 1;
						}
					}

				Material material = Material.getMaterial(materialId);
				List<BlockRecord> blocksAffected = Commands.buildStructure(startLocation, dimensions, material, args.length > 4);
				Commands.updateUndoAndRedoStacks(blocksAffected, p);

				return true;
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
				Commands.updateBlocks(loc1, loc2, Material.getMaterial(1));

				p.sendMessage("Structure has been constructed in the region marked.");
				return true;
			}
			case "eyebuild": {
				if (args.length < 3) {
					p.sendMessage("Not enough parameters.");
					break;
				}

				ProcessBuilder eyeTrack = new ProcessBuilder();
				final String spath = jarLocation + eyeTrackExecutable;
				eyeTrack.command(spath);

				new BukkitRunnable() {
					@Override
					public void run() {
						p.sendMessage("Tracking eyes...");
						p.sendMessage("Building Structure in 10 seconds...");
						try { Runtime.getRuntime().exec(spath); } catch (Exception e) { e.printStackTrace(); }
					}
				}.runTaskAsynchronously(this.plugin);

				new BukkitRunnable() {
					@Override
					public void run() {
						p.sendMessage("Building Structure...");
						String mmbuild_args = String.join(" ", args);

						Bukkit.getPlayer(p.getUniqueId()).performCommand("mmbuild " + mmbuild_args);
					}
				}.runTaskLater(this.plugin, 200);

				return true;
			}
			case "eyetrack": {
				if (args.length != 1) {
					p.sendMessage("Improper number of parameters.");
					break;
				}
				else if (args[0].equalsIgnoreCase("on") && !eyeTracking) {
					ProcessBuilder eyeTrack = new ProcessBuilder();
					final String spath = jarLocation + eyeTrackExecutable;
					eyeTrack.command(spath);

					eyeTrackRunnable = new BukkitRunnable() {
						@Override
						public void run() {
							try {
								Runtime.getRuntime().exec(spath);
								eyeTracking = true;
							}
							catch (Exception e) { e.printStackTrace(); }
						}
					}.runTaskTimerAsynchronously(this.plugin, 0, 200);

				} else if (args[0].equalsIgnoreCase("off")) {
					eyeTrackRunnable.cancel();
					eyeTracking = false;
				}
				return true;
			}
			case "msave": {
				if(args.length != 1) {
					p.sendMessage("Improper number of parameters.");
					break;
				}

				List<BlockRecord> playerBuildData;
				try { playerBuildData = PreviousBuildsData.getInstance().getPlayersBuildRecordForUndo(p).blocksAffected; }
				catch(NoCommandHistoryException e) {
					p.sendMessage("No previous builds found.");
					break;
				}

				BlockRecord first = playerBuildData.get(0);
				BlockRecord last = playerBuildData.get(playerBuildData.size() - 1);

				String[] entry = {args[0],
						Integer.toString(Math.abs(last.x - first.x) + 1),
						Integer.toString(Math.abs(last.y - first.y) + 1),
						Integer.toString(Math.abs(last.z - first.z) + 1),
						Integer.toString(p.getWorld().getBlockAt(first.x, first.y, first.z).getType().getId())};

				boolean overwrite = structureData.setStructureData(entry);
				p.sendMessage("Saved " + args[0] + ".");
				if(overwrite) p.sendMessage("Warning: Overwrote " + args[0] + ".");
				return true;
			}
			case "mclone": {
				if(args.length != 1) {
					p.sendMessage("Improper number of parameters");
					break;
				}

				String[] buildData = structureData.getStructureData(args[0]);
				if(buildData == null) {
					p.sendMessage(args[0] + " was not found.");
					break;
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
				structureData.setCopyLocation1(startLocation);
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
				structureData.setCopyLocation2(endLocation);
				p.sendMessage("The second position has been marked.");
				return true;
			}
			case "mpaste": {
				if(!p.isOp()) {
					p.sendMessage("This command requires op status.");
					break;
				}

				String copyArgs = structureData.getCopyArgs();
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