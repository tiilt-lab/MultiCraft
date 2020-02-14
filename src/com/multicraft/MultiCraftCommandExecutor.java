package com.multicraft;

import java.io.File;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
	private String eyeTrackExecutable;
	private BukkitTask eyeTrackRunnable;
	private CSVReadWrite csvManager;
	private List<List<String>> structureData;
	
	public MultiCraftCommandExecutor(MultiCraft plugin) {
		this.plugin = plugin;
		File filePath = new File(MultiCraftCommandExecutor.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		jarLocation = filePath.getPath().substring(0, filePath.getPath().indexOf(filePath.getName()));
		eyeTrackExecutable = "Tobii" + File.separator + "Interaction_Streams_101.exe";
		csvManager = new CSVReadWrite();
		structureData = csvManager.readCSV(jarLocation + "StructureData.csv");
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
				List<Block> blocks = p.getLineOfSight((Set<Material>) null, 6);

				// get Start Location, defaults to the block in front of the player
				Location startLocation = blocks.get(0).getLocation();
				for (Block b : blocks) {
					if (!b.getType().equals(Material.AIR)) {
						startLocation = b.getLocation().add(0, 1, 0);
					}
				}

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

				p.sendMessage("Please select the first position by pointing at it with a cursor and issueing the command"
						+ " /loc1, then select the second position by pointing at it with the cursor and issuing command"
						+ " /loc2. If the region is in the air, the part coordinates will be set as your position");
				break;
			}
			case "loc1": {
				RegionBuild rBuild = RegionBuild.getInstance();
				List<Block> blocks = p.getLineOfSight((Set<Material>) null, 6);
				Location startLocation = null;

				for (Block b : blocks)
					if (!b.getType().equals(Material.AIR))
						startLocation = b.getLocation().add(0, 1, 0);

				if (startLocation == null) startLocation = p.getLocation();
				rBuild.markStartPosition(p, startLocation);

				p.sendMessage("The first position has been marked.");
				return true;
			}
			case "loc2": {
				RegionBuild rBuild = RegionBuild.getInstance();
				List<Block> blocks = p.getLineOfSight((Set<Material>) null, 6);
				Location endLocation = null;

				for (Block b : blocks)
					if (!b.getType().equals(Material.AIR))
						endLocation = b.getLocation().add(0, 1, 0);

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
				if (args.length < 3)
					break;

				ProcessBuilder eyeTrack = new ProcessBuilder();
				final String spath = jarLocation + eyeTrackExecutable;
				eyeTrack.command(spath);

				new BukkitRunnable() {
					@Override
					public void run() {
						p.sendMessage("Tracking eyes...");
						p.sendMessage("Building Structure in 10 seconds...");
						try {
							Runtime.getRuntime().exec(spath);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.runTaskAsynchronously(this.plugin);

				new BukkitRunnable() {
					@Override
					public void run() {
						p.sendMessage("Building Structure...");
						String mmbuild_args = " " + args[0] + " " + args[1] + " " + args[2];
						if (args.length > 3) mmbuild_args += " " + args[3];

						Bukkit.getPlayer(p.getUniqueId()).performCommand("mmbuild" + mmbuild_args);
					}
				}.runTaskLater(this.plugin, 200);

				return true;
			}
			case "eyetrack": {
				if (args.length != 1)
					break;
				else if (args[0].equalsIgnoreCase("on")) {
					ProcessBuilder eyeTrack = new ProcessBuilder();
					final String spath = jarLocation + eyeTrackExecutable;
					eyeTrack.command(spath);

					eyeTrackRunnable = new BukkitRunnable() {
						@Override
						public void run() {
							try {
								Runtime.getRuntime().exec(spath);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}.runTaskTimerAsynchronously(this.plugin, 0, 200);
				} else if (args[0].equalsIgnoreCase("off"))
					eyeTrackRunnable.cancel();
				return true;
			}
			case "mstore": {
			}
			case "mclone": {
			}
			default:
				p.sendMessage("No MultiCraft commands executed.");
				break;
		}
		return false;
	}
}