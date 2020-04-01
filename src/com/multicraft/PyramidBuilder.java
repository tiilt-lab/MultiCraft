package com.multicraft;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.multicraft.Materials.MaterialDoesNotExistException;

public class PyramidBuilder implements CommandExecutor {
	private final MultiCraft plugin;
	
	public PyramidBuilder(MultiCraft pl) {
		this.plugin = pl;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("pyramid")) {
			// p.sendMessage("The pyramid command has been called");
			// p.sendMessage(Integer.toString(args.length));
			if(args.length < 1) {
				p.sendMessage("Not enough parameters.");
				return false;
			}
			
			// parse args to dimensions
			int size = Integer.parseInt(args[0]);
			Location playerLoc = p.getLocation();			

			int materialId = 1;
			if(args.length > 1) {
				try { materialId = Integer.parseInt(args[1]); }
				catch(NumberFormatException e) {
					try { materialId = Materials.getId(args[1]); }
					catch(MaterialDoesNotExistException f) { materialId = 1; }
				}
			}
			Material material = Material.getMaterial(materialId);

			boolean hollow = args.length > 2;
			makePyramid(new BlockVector3(playerLoc.getX(), playerLoc.getY(), playerLoc.getZ()), material, size, hollow, playerLoc.getWorld());
			
			return true;
		}
		return false;
	}
	
	
	public void makePyramid(BlockVector3 position, Material block, int size, boolean hollow, World w){
		// plugin.getServer().broadcastMessage(position.toString());
        int height = size;
        for (int y = 0; y <= height; ++y) {
            size--;
            for (int x = 0; x <= size; ++x)
                for (int z = 0; z <= size; ++z)
                    if ((!hollow && z <= size && x <= size) || z == size || x == size) {
                    	byte tempByte = (byte) 0;
                    	updateBlock(w, (int) position.x + x, (int) position.y + y, (int) position.z + z, block, tempByte);
                    	updateBlock(w, (int) position.x - x, (int) position.y + y, (int) position.z + z, block, tempByte);
                    	updateBlock(w, (int) position.x + x, (int) position.y + y, (int) position.z - z, block, tempByte);
                    	updateBlock(w, (int) position.x - x, (int) position.y + y, (int) position.z - z, block, tempByte);
                    }
        }
    }
	
	private void updateBlock(World world, int x, int y, int z, Material blockType, byte blockData) {
        Block thisBlock = world.getBlockAt(x,y,z);
        updateBlock(thisBlock, blockType, blockData);
    }
	
	private void updateBlock(Block block, Material m, byte blockData) {
        try { block.setType(m); }
        catch(Exception e) {
			// TODO: Handle this mess. At the moment, it updates block async, which raises an error every time. This is an illegal fix :(
        	block.getState().update();
        }
    }
	
	public static class BlockVector3{
		public double x;
		public double y;
		public double z;
		public BlockVector3(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

}
