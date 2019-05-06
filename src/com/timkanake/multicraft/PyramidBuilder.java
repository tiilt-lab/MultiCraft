package com.timkanake.multicraft;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.timkanake.multicraft.Materials.MaterialDoesNotExistException;


// TODO: Currently this build the pyramid in a very weird way,
// check to see the world edit format and utilize the setBlock function that they are using
// 
public class PyramidBuilder implements CommandExecutor {
	private final MultiCraft plugin;
	
	
	public PyramidBuilder(MultiCraft pl) {
		this.plugin = pl;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("pyramid")) {
			plugin.getServer().broadcastMessage("The pyramid command has been called");
			plugin.getServer().broadcastMessage(Integer.toString(args.length));
			// TODO: Give user feedback
			if(args.length < 1)
				return false;
			
			// parse args to dimensions
			int size = Integer.parseInt(args[0]);
			
			Location playerLoc = p.getLocation();			
			
			// TODO: Get the material
			int materialId = 1;
			if(args.length > 1) {
				try {
					materialId = Integer.parseInt(args[1]);
				}catch(NumberFormatException e) {
					try {
						materialId = Materials.getId(args[1]);
					}catch(MaterialDoesNotExistException f){
						materialId = 1;
					}
				}
			}
			Material material = Material.getMaterial(materialId);
			
			// TODO: Check if Hollow
			boolean hollow = false;
			if(args.length > 2) {
				hollow = true;
			}
			
			makePyramid(new BlockVector3(playerLoc.getX(), playerLoc.getY(), playerLoc.getZ()), material, size, hollow, playerLoc.getWorld());
			
			return true;
		}
		
		return false;
	}
	
	
	public void makePyramid(BlockVector3 position, Material block, int size, boolean hollow, World w){
		plugin.getServer().broadcastMessage(position.toString());
        int height = size;
        for (int y = 0; y <= height; ++y) {
            size--;
            for (int x = 0; x <= size; ++x) {
                for (int z = 0; z <= size; ++z) {

                    if ((hollow && z <= size && x <= size) || z == size || x == size) {
                    	// one
                    	byte tempByte = (byte) 0;
                    	
                    	updateBlock(w, (int) position.x + x, (int) position.y + y, (int) position.z + z, block, tempByte);
                    	
                    	updateBlock(w, (int) position.x - x, (int) position.y + y, (int) position.z + z, block, tempByte);
                    	
                    	updateBlock(w, (int) position.x + x, (int) position.y + y, (int) position.z - z, block, tempByte);
                    	
                    	updateBlock(w, (int) position.x - x, (int) position.y + y, (int) position.z - z, block, tempByte);
                    }
                }
            }
        }
    }
	
	private void updateBlock(World world, int x, int y, int z, Material blockType, byte blockData) {
        Block thisBlock = world.getBlockAt(x,y,z);
        updateBlock(thisBlock, blockType, blockData);
    }
	
	private void updateBlock(Block block, Material m, byte blockData) {
        try {
            block.setType(m);
        }catch(Exception e) {
            plugin.getServer().broadcastMessage(e.toString());
            plugin.getServer().broadcastMessage("Failed to update Blocks :(");
        }
    }
	
	private class BlockVector3{
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
