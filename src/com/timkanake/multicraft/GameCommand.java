package com.timkanake.multicraft;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.timkanake.multicraft.PyramidBuilder.BlockVector3;
import com.timkanake.multicraft.PyramidBuilder;


/*
 * An object representation of a game command for MultiCraft
 */


public class GameCommand {
	String commandName;
	Player issuer;
	JSONObject args;
	MultiCraft plugin;
	
	
	public GameCommand(JSONObject argss, MultiCraft pl) {
		plugin = pl;
		args = argss;
		commandName = (String) args.get("command");
		issuer = (Player) Bukkit.getPlayer(UUID.fromString((String) args.get("client_name")));		
	}
	
	public GameCommand(MultiCraft pl) {
		this.plugin = pl;
	}
	
	
	public boolean commandSupported(String com) {
		return CommandWords.getInstance().commands.contains(com);
	}
	
	public boolean execute() {
		if(! commandSupported(commandName) || ! playerIsOnline(issuer)) {
			return false;
		}
		if(commandName.equals("build")) {
			return executeBuild();
		}else if(commandName.equals("move")) {
			return executeMove();
		}else if(commandName.equals("undo")) {
			return executeUndo();
		}else if(commandName.equals("redo")) {
			return executeRedo();
		}
		return false;
	}
	
	public boolean executeUndo() {
		return Commands.undo(this.issuer, this.plugin);
	}
	
	public boolean executeRedo() {
		return Commands.redo(this.issuer, this.plugin);
	}
	
	@SuppressWarnings("deprecation")
	public boolean executeBuild() {
		JSONArray dimAr = (JSONArray) args.get("dimensions");
		int[] dimensions = new int[3];
		dimensions[0] = ((Long) dimAr.get(0)).intValue();
		dimensions[1] = ((Long) dimAr.get(1)).intValue();
		dimensions[2] = ((Long) dimAr.get(2)).intValue();
		
		//TODO: Update so that the location is at the cursor
		Location l = issuer.getLocation();
		
		
		int id = ((Long) args.get("block_code")).intValue();
		Material m = Material.getMaterial(id);
		
		
		// TODO: Clean up this logic
		if (args.containsKey("roof") && (Boolean) args.get("roof")) {
			PyramidBuilder tempBuilder = new PyramidBuilder(this.plugin);
			tempBuilder.makePyramid(new BlockVector3(l.getX(), l.getY(), l.getZ()), m, dimensions[0], true, issuer.getWorld());
			return true;
		}
		
		boolean isHollow = (args.containsKey("hollow") && (Boolean) args.get("hollow")) ? true : false;
		
		List<BlockRecord> blocksAffected = Commands.buildStructure(l, dimensions, m, isHollow);
//		int[] buildCoordinates = CoordinateCalculations.getBuildCoordinates(l, dimensions);
//		Location l2 = new Location(l.getWorld(), buildCoordinates[0], buildCoordinates[1], buildCoordinates[2]);
//		
//		
//		if(args.containsKey("hollow") && (Boolean) args.get("hollow")) {
//			buildHollow(dimensions, l, l2, this, m);
//		}else {
//			updateBlocks(l, l2, m);
//		}
		Commands.updateUndoAndRedoStacks(blocksAffected, this.issuer);
		return true;
	}
	
	public static List<BlockRecord>  updateBlocks(Location pos1, Location pos2, Material m) {
		int minX, maxX, minY, maxY, minZ, maxZ;
		World world = pos1.getWorld();
		minX = pos1.getBlockX() < pos2.getBlockX() ? pos1.getBlockX() : pos2.getBlockX();
		maxX = pos1.getBlockX() >= pos2.getBlockX() ? pos1.getBlockX() : pos2.getBlockX();
		minY = pos1.getBlockY() < pos2.getBlockY() ? pos1.getBlockY() : pos2.getBlockY();
		maxY = pos1.getBlockY() >= pos2.getBlockY() ? pos1.getBlockY() : pos2.getBlockY();
		minZ = pos1.getBlockZ() < pos2.getBlockZ() ? pos1.getBlockZ() : pos2.getBlockZ();
		maxZ = pos1.getBlockZ() >= pos2.getBlockZ() ? pos1.getBlockZ() : pos2.getBlockZ();
		
		List<BlockRecord> blocksAffected = new ArrayList<BlockRecord>();

		for (int x = minX; x <= maxX; ++x) {
			for (int z = minZ; z <= maxZ; ++z) {
				for (int y = minY; y <= maxY; ++y) {
					blocksAffected.add(updateBlock(world, x, y, z, m,(byte) 0));
				}
			}
		}
		return blocksAffected;
	}
	
	public void buildHollow(int[] dimensions, Location startLoc, Location endLoc, GameCommand gComm, Material m) {		
		// bottom Wall
		updateBlocks(startLoc, new Location(endLoc.getWorld(), endLoc.getX(), startLoc.getY(), endLoc.getZ()), m);
		
		// top wall
		updateBlocks(new Location(startLoc.getWorld(), startLoc.getX(), endLoc.getY(), startLoc.getZ()), 
				endLoc, m);
		
		// back wall
		updateBlocks(new Location(startLoc.getWorld(), startLoc.getX(), startLoc.getY(), endLoc.getZ()), endLoc, m);
		
		// front wall
		updateBlocks(startLoc, new Location(endLoc.getWorld(), endLoc.getX(), endLoc.getY(), startLoc.getZ()), m);

		// right wall
		updateBlocks(new Location(startLoc.getWorld(), endLoc.getX(), startLoc.getY(), startLoc.getZ()), endLoc, m);
		
		// left wall
		updateBlocks(startLoc, new Location(endLoc.getWorld(), startLoc.getX(), endLoc.getY(), endLoc.getZ()), m);
	}

	private static BlockRecord updateBlock(World world, int x, int y, int z, Material blockType, byte blockData) {
		Block thisBlock = world.getBlockAt(x,y,z);
		return updateBlock(thisBlock, blockType, blockData);
		
	}

	private static BlockRecord updateBlock(Block block, Material m, byte blockData) {
		BlockRecord toReturn = new BlockRecord(block.getType(), block.getX(), block.getY(), block.getZ());
		try {			
			block.setType(m);
		}catch(Exception e) {
			// TODO: Handle this mess
		}
		return toReturn;
	}
	
	public boolean executeMove() {
		int distanceToMove = ((Long) args.get("dimensions")).intValue();
		Location pLoc = issuer.getLocation();
		Location newLoc = pLoc.add(distanceToMove, 0, 0);
		issuer.teleport(newLoc);
		return true;
	}

	public Player getPlayerFromUUID(String uuid) {
		UUID id = UUID.fromString(uuid);
		return (Player) Bukkit.getOfflinePlayer(id);
	}
	
	public boolean playerIsOnline(Player p) {
		return Bukkit.getOnlinePlayers().contains(p);
	}	
}
