package com.multicraft;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.multicraft.Materials.MaterialDoesNotExistException;

import javax.swing.plaf.synth.Region;

public class MultiCraftCommandExecutor implements CommandExecutor{
	private final MultiCraft plugin;
	
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

			try {
				File fpath = new File(MultiCraftCommandExecutor.class.getProtectionDomain().getCodeSource().getLocation().getPath());
				String spath = fpath.getPath();
				spath = spath.substring(0, spath.indexOf(fpath.getName())) + "Tobii" + File.separator + "Interaction_Streams_101.exe";

				Runtime run = Runtime.getRuntime();
				Process proc = run.exec(spath);

				Thread.sleep(3000);
				proc.destroy();

			} catch (Exception e) {
				e.printStackTrace();
			}

			int[] dimensions = new int[] {Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])};
			Location startLocation = p.getTargetBlock((Set<Material>) null, 20).getLocation();

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
		}
		return false;
	}
}
