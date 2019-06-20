package com.timkanake.multicraft;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.timkanake.multicraft.Materials.MaterialDoesNotExistException;

public class MultiCraftCommandExecutor implements CommandExecutor{
	private final MultiCraft plugin;
	
	public MultiCraftCommandExecutor(MultiCraft plugin) {
		this.plugin = plugin;
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		if(cmd.getName().equalsIgnoreCase("mbuild")) {
			// TODO: Give user feedback if there's an error in command format
			if(args.length < 3)
				return false;
			
			int[] dimensions = new int[] {Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])};
			
			// Alternative to start of building location, this will build the structure next to the player 
			Location startLocation = p.getLocation(); 
			
//			List<Block> blocks = p.getLineOfSight((Set<Material>) null, 6);
//			
//			
//			Material tempMaterial = null;
//			
//			// get Start Location, defaults to the block in front of the player
//			Location startLocation = blocks.get(0).getLocation();
//			for(Block b : blocks) {
//				tempMaterial = b.getType();
//				if(! tempMaterial.equals(Material.AIR)) {
//					startLocation = b.getLocation().add(0, 1, 0);
//				}
//			}
			
			int materialId = 1;			
			if(args.length > 3) {
				try {
					materialId = Integer.parseInt(args[3]);
				}catch(NumberFormatException e) {
					try {
						materialId = Materials.getId(args[3]);
					}catch(MaterialDoesNotExistException f) {
						// TODO: Give feedback to the user
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
			
			// Alternative to start of building location, this will build the structure next to the player 
			// Location playerLoc = p.getLocation(); 
			
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
						// TODO: Give feedback to the user
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
		}
		return false;
	}
}
