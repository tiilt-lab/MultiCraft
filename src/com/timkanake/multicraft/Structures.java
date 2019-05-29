package com.timkanake.multicraft;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;


public class Structures {

    public static MultiCraft plugin;

    @SuppressWarnings("static-access")
	public Structures(MultiCraft pl){
        this.plugin = pl;
    }


    public void makePyramid(Location position, Material block, int size, boolean hollow){

        int height = size;

        for (int y = 0; y <= height; ++y) {
            size--;
            for (int x = 0; x <= size; ++x) {
                for (int z = 0; z <= size; ++z) {

                    if ((hollow && z <= size && x <= size) || z == size || x == size) {
                        World w = position.getWorld();
                        byte tempByte = (byte) 0;
                        updateBlock(w, x, y, z, block, tempByte);
                        updateBlock(w, -x, y, z, block, tempByte);
                        updateBlock(w, x, y, -z, block, tempByte);
                        updateBlock(w, -x, y, -z, block, tempByte);
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
}
