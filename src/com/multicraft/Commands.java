package com.multicraft;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import static java.lang.Math.*;

public class Commands {

    public static boolean undo(Player p, MultiCraft plugin) {
        PreviousBuildsData pData = PreviousBuildsData.getInstance();
        BuildCommandData playerBuildRecord;

        // get the player's build record
        try {
            playerBuildRecord = pData.getPlayersBuildRecordForUndo(p);
        } catch (NoCommandHistoryException e) {
            p.sendMessage("You have no build record available.");
            return false;
        }

        // restore blocks
        List<BlockRecord> blocksToChange = playerBuildRecord.blocksAffected;
        List<BlockRecord> blocksAffectedDuringUndo = new ArrayList<>();
        World world = p.getWorld();
        for (BlockRecord b : blocksToChange) {
            Block t = world.getBlockAt(b.x, b.y, b.z);
            blocksAffectedDuringUndo.add(new BlockRecord(t.getType(), t.getX(), t.getY(), t.getZ()));
            Bukkit.getScheduler().runTask(plugin, () -> t.setType(b.material));
        }

        // update re-do stack
        BuildCommandData toStoreInRedo = new BuildCommandData(blocksAffectedDuringUndo, blocksAffectedDuringUndo.size());
        pData.addToRedoStack(p, toStoreInRedo);
        return true;
    }

    public static boolean redo(Player p, MultiCraft plugin) {
        // get data from redoStack
        PreviousBuildsData pData = PreviousBuildsData.getInstance();
        BuildCommandData playerBuildRecord;

        try {
            playerBuildRecord = pData.getPlayersBuildRecordForRedo(p);
        } catch (NoCommandHistoryException e) {
            p.sendMessage("You have no build record available for redo.");
            return false;
        }

        List<BlockRecord> blocksToChange = playerBuildRecord.blocksAffected;
        List<BlockRecord> blocksAffectedDuringRedo = new ArrayList<>();
        World world = p.getWorld();
        for (BlockRecord b : blocksToChange) {
            Block t = world.getBlockAt(b.x, b.y, b.z);
            blocksAffectedDuringRedo.add(new BlockRecord(t.getType(), t.getX(), t.getY(), t.getZ()));
            Bukkit.getScheduler().runTask(plugin, () -> t.setType(b.material));
        }

        // restore blocks
        BuildCommandData toStoreInUndo = new BuildCommandData(blocksAffectedDuringRedo, blocksAffectedDuringRedo.size());
        pData.addToUndoStack(p, toStoreInUndo);
        return true;
    }

    public static List<BlockRecord> buildStructure(Location playerLoc, Location startLoc, int[] dimensions, Material m, boolean isHollow, MultiCraft plugin) {
        int[] buildCoordinates = CoordinateCalculations.getBuildCoordinates(playerLoc, startLoc, dimensions);
        Location endLoc = new Location(startLoc.getWorld(), buildCoordinates[0], buildCoordinates[1], buildCoordinates[2]);

        return updateBlocks(startLoc, endLoc, m, isHollow, null, plugin);
    }

    public static List<BlockRecord> buildTangiStructure(Location playerLoc, Location startLoc, JSONObject blockMap, MultiCraft plugin) {
        int[] dimensions = {10, 10, 10};
        int[] buildCoordinates = CoordinateCalculations.getBuildCoordinates(playerLoc, startLoc, dimensions);
        Location endLoc = new Location(startLoc.getWorld(), buildCoordinates[0], buildCoordinates[1], buildCoordinates[2]);

        return updateBlocks(startLoc, endLoc, Material.STONE, false, blockMap, plugin);
    }

    @SuppressWarnings("deprecation")
    public static List<BlockRecord> updateBlocks(Location pos1, Location pos2, Material m, Boolean hollow, JSONObject blockMap, MultiCraft plugin) {
        int startX = min(pos1.getBlockX(), pos2.getBlockX());
        int startY = min(pos1.getBlockY(), pos2.getBlockY());
        int startZ = min(pos1.getBlockZ(), pos2.getBlockZ());
        int endX = max(pos1.getBlockX(), pos2.getBlockX());
        int endY = max(pos1.getBlockY(), pos2.getBlockY());
        int endZ = max(pos1.getBlockZ(), pos2.getBlockZ());

        World world = pos1.getWorld();
        List<BlockRecord> blocksAffected = new ArrayList<>();

        for (int x = startX; x <= endX; x++) {
            String relativeX = Integer.toString(x - startX);
            JSONObject blockMapY = null;
            if (blockMap != null) {
                if (!blockMap.containsKey(relativeX)) continue;
                else blockMapY = (JSONObject) blockMap.get(relativeX);
            }
            for (int y = startY; y <= endY; y++) {
                String relativeY = Integer.toString(y - startY);
                JSONObject blockMapZ = null;
                if (blockMapY != null) {
                    if (!blockMapY.containsKey(relativeY)) continue;
                    else blockMapZ = ((JSONObject) blockMapY.get(relativeY));
                }
                for (int z = startZ; z <= endZ; z++) {
                    String relativeZ = Integer.toString(z - startZ);
                    if (hollow && !((x == startX || x == endX) && (y == startY || y == endY) && (z == startZ || z == endZ))) continue;
                    else if (blockMapZ != null) {
                        if (!blockMapZ.containsKey(relativeZ)) continue;
                        else {
                            int id = ((Long) blockMapZ.get(relativeZ)).intValue();
                            m = Material.getMaterial(id);
                        }
                    }
                    blocksAffected.add(updateBlock(world, plugin, x, y, z, m));
                }
            }
        }

        return blocksAffected;
    }

    private static BlockRecord updateBlock(World world, MultiCraft plugin, int x, int y, int z, Material blockType) {
        Block thisBlock = world.getBlockAt(x, y, z);
        BlockRecord toReturn = new BlockRecord(thisBlock.getType(), x, y, z);
        Bukkit.getScheduler().runTask(plugin, () -> thisBlock.setType(blockType));

        return toReturn;
    }

    public static void updateUndoAndRedoStacks(List<BlockRecord> blocksAffected, Player p) {
        BuildCommandData affectedBlocksData = new BuildCommandData(blocksAffected, blocksAffected.size());
        PreviousBuildsData pData = PreviousBuildsData.getInstance();
        pData.clearPlayerRedo(p);
        pData.appendBuildRecord(p, affectedBlocksData);
    }

}
