package com.timkanake.multicraft;

import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


// TODO: Currently this build the pyramid in a very weird way,
// check to see the world edit format and utilize the setBlock function that they are using
// 
public class RayTracingTest implements CommandExecutor {
	private final MultiCraft plugin;
	
	
	public RayTracingTest(MultiCraft pl) {
		this.plugin = pl;
	}

	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		Player p = (Player) sender;
		World w = p.getWorld();
		if(cmd.getName().equalsIgnoreCase("ray")) {
			plugin.getServer().broadcastMessage("The ray command has been called");
			// plugin.getServer().broadcastMessage(Integer.toString(args.length));
			// TODO: Give user feedback
			List<Block> blocks = p.getLineOfSight((Set<Material>) null, 5);
			
			for(Block b: blocks) {
				Location tempLoc = b.getLocation();
				updateBlock(w, (int) tempLoc.getX(), (int) tempLoc.getY(), (int) tempLoc.getZ(), Material.getMaterial(1), (byte) 0);	
			}
			
			plugin.getServer().broadcastMessage(Integer.toString(blocks.size()));
			return true;
		}
		
		return false;
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
}

