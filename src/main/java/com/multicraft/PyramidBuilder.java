package com.multicraft;

import com.multicraft.Materials.MaterialDoesNotExistException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PyramidBuilder implements CommandExecutor {
	private final MultiCraft plugin;
	
	public PyramidBuilder(MultiCraft plugin) {
		this.plugin = plugin;
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

			List<BlockRecord> blocksAffected =  makePyramid(new BlockVector3(playerLoc.getX(), playerLoc.getY(), playerLoc.getZ()), material, size, hollow, playerLoc.getWorld());
			Commands.updateUndoAndRedoStacks(blocksAffected, p);
			return true;
		}
		return false;
	}
	
	
	public List<BlockRecord> makePyramid(BlockVector3 position, Material block, int size, boolean hollow, World w){
		// plugin.getServer().broadcastMessage(position.toString());
        int height = size;

        List<BlockRecord> blocksAffected = new ArrayList<>();

        for (int y = 0; y <= height; ++y) {
            size--;
            for (int x = 0; x <= size; ++x)
                for (int z = 0; z <= size; ++z)
                    if ((!hollow && z <= size && x <= size) || z == size || x == size) {
                    	byte tempByte = (byte) 0;
                    	blocksAffected.add(updateBlock(w, (int) position.x + x, (int) position.y + y, (int) position.z + z, block, tempByte));
                    	blocksAffected.add(updateBlock(w, (int) position.x - x, (int) position.y + y, (int) position.z + z, block, tempByte));
                    	blocksAffected.add(updateBlock(w, (int) position.x + x, (int) position.y + y, (int) position.z - z, block, tempByte));
                    	blocksAffected.add(updateBlock(w, (int) position.x - x, (int) position.y + y, (int) position.z - z, block, tempByte));
                    }
        }

        return blocksAffected;
    }
	
	private BlockRecord updateBlock(World world, int x, int y, int z, Material blockType, byte blockData) {
        Block thisBlock = world.getBlockAt(x,y,z);
        return updateBlock(thisBlock, blockType, blockData);
    }
	
	private BlockRecord updateBlock(Block block, Material m, byte blockData) {
		BlockRecord toReturn = new BlockRecord(block.getType(), block.getX(), block.getY(), block.getZ());
		Bukkit.getScheduler().runTask(plugin, () -> { block.setType(m); });
		return toReturn;
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
