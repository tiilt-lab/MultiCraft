package com.multicraft;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.*;
import jdk.nashorn.internal.runtime.PropertyAccess;
import org.apache.logging.log4j.core.helpers.Integers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.multicraft.Materials.MaterialDoesNotExistException;
import org.bukkit.scheduler.BukkitRunnable;


public class MultiCraftCommandExecutor implements CommandExecutor{
	private final MultiCraft plugin;
	private Hashtable<String, String[]> names;
	
	public MultiCraftCommandExecutor(MultiCraft plugin) {
		this.plugin = plugin;
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		// TODO: Check if you can make this a switch case
		
		if(cmd.getName().equalsIgnoreCase("mbuild")) {
			// TODO: Give user feedback if there's an error in command format
			if(args.length < 3)
				return false;
			
			int[] dimensions = new int[] {Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])}; 
			Location startLocation = p.getLocation(); 
			
			int materialId = 1;			
			if(args.length > 3) {
				try {
					materialId = Integer.parseInt(args[3]);
				}catch(NumberFormatException e) {
					try {
						materialId = Materials.getId(args[3]);
					}catch(MaterialDoesNotExistException f) {
						p.sendMessage("The material you specified does not exists. Defaulting to stone.");
						materialId = 1;
					}
				}
			}			
			
			Material material = Material.getMaterial(materialId);
			
			List<BlockRecord> blocksAffected = new ArrayList<BlockRecord>();
			blocksAffected = Commands.buildStructure(startLocation,  dimensions, material, args.length > 4);
			
			Commands.updateUndoAndRedoStacks(blocksAffected, p);
			
			return true;
		}else if(cmd.getName().equalsIgnoreCase("mmbuild")) {
			// TODO: Give user feedback if there's an error in command format
			if(args.length < 3)
				return false;
			
			int[] dimensions = new int[] {Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])};
			
			List<Block> blocks = p.getLineOfSight((Set<Material>) null, 6);
			
			
			Material tempMaterial = null;
			
			// get Start Location, defaults to the block in front of the player
			Location startLocation = blocks.get(0).getLocation();
			for(Block b : blocks) {
				tempMaterial = b.getType();
				if(! tempMaterial.equals(Material.AIR)) {
					startLocation = b.getLocation().add(0, 1, 0);
				}
			}
			
			int materialId = 1;			
			if(args.length > 3) {
				try {
					materialId = Integer.parseInt(args[3]);
				}catch(NumberFormatException e) {
					try {
						materialId = Materials.getId(args[3]);
					}catch(MaterialDoesNotExistException f) {
						p.sendMessage("The material you specified does not exists. Defaulting to stone.");
						materialId = 1;
					}
				}
			}			
			
			Material material = Material.getMaterial(materialId);
			
			List<BlockRecord> blocksAffected = new ArrayList<BlockRecord>();
			blocksAffected = Commands.buildStructure(startLocation,  dimensions, material, args.length > 4);
			
			Commands.updateUndoAndRedoStacks(blocksAffected, p);
			
			return true;
		}else if(cmd.getName().equalsIgnoreCase("mundo")) {
			return Commands.undo(p, this.plugin);
		}else if(cmd.getName().equalsIgnoreCase("mredo")) {
			return Commands.redo(p, this.plugin);
		}else if(cmd.getName().equalsIgnoreCase("rbuild")) {
			RegionBuild rBuild = RegionBuild.getInstance();
			rBuild.startRegionBuildForPlayer(p);
			
			p.sendMessage("Please select the first position by pointing at it with a cursor and issueing the command"
					+ " /loc1, then select the second position by pointing at it with the cursor and issuing command"
					+ " /loc2. If the region is in the air, the part coordinates will be set as your position");
		}else if(cmd.getName().equalsIgnoreCase("loc1")) {
			RegionBuild rBuild = RegionBuild.getInstance();
			List<Block> blocks = p.getLineOfSight((Set<Material>) null, 6);
			Location startLocation = null;
			Material tempMaterial = null;
			
			for(Block b : blocks) {
				tempMaterial = b.getType();
				if(! tempMaterial.equals(Material.AIR)) {
					startLocation = b.getLocation().add(0, 1, 0);
				}
			}
			
			if(startLocation == null)
				startLocation = p.getLocation();
			
			rBuild.markStartPosition(p, startLocation);
			
			p.sendMessage("The first position has been marked");
			
			return true;
		}else if(cmd.getName().equalsIgnoreCase("loc2")) {
			RegionBuild rBuild = RegionBuild.getInstance();
			List<Block> blocks = p.getLineOfSight((Set<Material>) null, 6);
			Location endLocation = null;
			Material tempMaterial = null;
			
			for(Block b : blocks) {
				tempMaterial = b.getType();
				if(! tempMaterial.equals(Material.AIR)) {
					endLocation = b.getLocation().add(0, 1, 0);
				}
			}
			
			if(endLocation == null)
				endLocation = p.getLocation();
			
			
			boolean succeeded = rBuild.markEndPosition(p, endLocation);
			
			if(! succeeded) {
				p.sendMessage("You cannot mark an end location without beginning a valid region"
						+ " build session");
				return false;
			}
			
			p.sendMessage("The second position has been marked");
			
			return true;
		}else if(cmd.getName().equalsIgnoreCase("rrbuild")) {
			RegionBuild rBuild = RegionBuild.getInstance();

			Location loc1 = rBuild.getStartLocation(p);
			Location loc2 = rBuild.getEndLocation(p);

			Commands.updateBlocks(loc1, loc2, Material.getMaterial(1));
			p.sendMessage("Structure has been constructed in the region marked");
		}else if(cmd.getName().equalsIgnoreCase("eyebuild")) {
			if(args.length < 3)
				return false;

			ProcessBuilder processBuilder = new ProcessBuilder();

			File fpath = new File(MultiCraftCommandExecutor.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			final String spath = fpath.getPath().substring(0, fpath.getPath().indexOf(fpath.getName())) + "Tobii" + File.separator + "Interaction_Streams_101.exe";
			processBuilder.command(spath);

			new BukkitRunnable() {
				@Override
				public void run(){
					p.sendMessage("Tracking eyes...");

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
					if(args.length > 3) mmbuild_args += " " + args[3];

					Bukkit.getPlayer(p.getUniqueId()).performCommand("mmbuild" + mmbuild_args);
				}
			}.runTaskLater(this.plugin, 200);

			return true;
		} else if (cmd.getName().equalsIgnoreCase("mstore")) {
			if (args.length < 7) {
				sender.sendMessage("Not enough parameters");
				return false;
			} else {
				String[] temp = {args[1], args[2], args[3], args[4], args[5], args[6]};
				names.put(args[0], temp);
				return true;
			}
		} else if (cmd.getName().equalsIgnoreCase("mclone")) {
			if (args.length < 4) {
				sender.sendMessage("Not enough parameters");
				return false;
			} else {
				String[] c = names.get(args[0]);
				if (c == null) {
					sender.sendMessage("Name doesn't exist.");
					return false;
				}
				String command = "clone";
				for (String i: c) {
					command = command + " " + i;
				}
				for (int i = 1; i <= 3; i++) {
					command = command + " " + args[i];
				}
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
				return true;
			}
		}
		//add marc's work
		return false;
	}
}