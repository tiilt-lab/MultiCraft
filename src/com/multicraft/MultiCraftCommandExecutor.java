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
import com.multicraft.Materials.MaterialDoesNotExistException;


public class MultiCraftCommandExecutor implements CommandExecutor {
	private final MultiCraft plugin;
	private final String jarLocation;
	private final String eyeTrackLocation;
	private boolean eyeTracking;
	private final CopyHandler copyHandler;


	public MultiCraftCommandExecutor(MultiCraft plugin) {
		this.plugin = plugin;
		File filePath = new File(MultiCraftCommandExecutor.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		jarLocation = filePath.getPath().substring(0, filePath.getPath().indexOf(filePath.getName()));
		eyeTrackLocation = jarLocation + "Tobii" + File.separator + "Interaction_Streams_101.exe";
		eyeTracking = false;
		copyHandler = new CopyHandler();
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
				List<BlockRecord> blocksAffected = Commands.buildStructure(startLocation, dimensions, material, args.length > 4, plugin);
				Commands.updateUndoAndRedoStacks(blocksAffected, p);

				return true;
			}
			case "mmbuild": {
				if (args.length < 1){
					p.sendMessage("Not enough parameters.");
					break;
				}
				if(args.length==1)
				{
					int dim1 = Integer.parseInt(args[0]);
					int[] dimensions = new int[]{dim1, dim1, dim1};
					Location startLocation = p.getTargetBlock((HashSet<Byte>) null, 16).getLocation().add(0, 1, 0);
					int materialId = 1;
					Material material = Material.getMaterial(materialId);
					List<BlockRecord> blocksAffected = Commands.buildStructure(startLocation, dimensions, material, args.length > 4, plugin);
					Commands.updateUndoAndRedoStacks(blocksAffected, p);
					return true;
				}
				else if (args.length==2)
				{
					int dim1 = Integer.parseInt(args[0]);
					int[] dimensions = new int[]{dim1, dim1, dim1};
					Location startLocation = p.getTargetBlock((HashSet<Byte>) null, 16).getLocation().add(0, 1, 0);

					int materialId = 1;

						try { materialId = Integer.parseInt(args[1]); } catch (NumberFormatException e) {
							try { materialId = Materials.getId(args[1]); } catch (MaterialDoesNotExistException f) {
								p.sendMessage("The material you specified does not exists. Defaulting to stone.");
								materialId = 1;
							}
						}

					Material material = Material.getMaterial(materialId);
					List<BlockRecord> blocksAffected = Commands.buildStructure(startLocation, dimensions, material, args.length > 4, plugin);
					Commands.updateUndoAndRedoStacks(blocksAffected, p);

					return true;

				}
				else if(args.length>=3){
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
				List<BlockRecord> blocksAffected = Commands.buildStructure(startLocation, dimensions, material, args.length > 4, plugin);
				Commands.updateUndoAndRedoStacks(blocksAffected, p);

				return true;}
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
				if (args.length < 3) {
					p.sendMessage("Not enough parameters.");
					break;
				}

				new BukkitRunnable() {
					@Override
					public void run() {
						Runtime run = Runtime.getRuntime();
						String[] eyeTrackCommand = {eyeTrackLocation, "-d"};
						try {
							p.sendMessage("Tracking eyes...");
							Process eyeTrack = run.exec(eyeTrackCommand);

							while (eyeTrack.isAlive()) { /* Wait for eye tracking executable to complete. */ }

							Bukkit.getScheduler().runTask(plugin, () -> {
								p.sendMessage("Building Structure...");
								String mmbuild_args = String.join(" ", args);
								Bukkit.getPlayer(p.getUniqueId()).performCommand("mmbuild " + mmbuild_args);
							});

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.runTaskAsynchronously(this.plugin);

				return true;
			}
			case "eyetrack": {
				if (!eyeTracking) {
					new BukkitRunnable() {
						@Override
						public void run() {
							Runtime run = Runtime.getRuntime();
							Process eyeTrack;
							String eyeTrackCommand = eyeTrackLocation;
							if(args.length == 1 && args[0].equalsIgnoreCase("move"))
								eyeTrackCommand += " -m";

							try {
								p.sendMessage("Tracking eyes, press . to end.");
								eyeTrack = run.exec(eyeTrackCommand);
								eyeTracking = true;

								while (eyeTrack.isAlive()) { /* Wait for eye tracking executable to complete. */ }

								p.sendMessage("No longer tracking eyes.");
								eyeTracking = false;

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}.runTaskAsynchronously(this.plugin);
				}

				return true;
			}
			case "mstore": {
				if(args.length != 1) {
					p.sendMessage("Improper number of parameters.");
					break;
				}

				StructureData universalStructureData = new StructureData(jarLocation + "universal" + "StructureData.csv");
				StructureData playerStructureData = new StructureData(jarLocation + p.getUniqueId() + "StructureData.csv");

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

				StructureData universalStructureData = new StructureData(jarLocation + "universal" + "StructureData.csv");
				StructureData playerStructureData = new StructureData(jarLocation + p.getUniqueId() + "StructureData.csv");

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