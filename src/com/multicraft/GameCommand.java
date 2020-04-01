package com.multicraft;

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
import com.multicraft.PyramidBuilder.BlockVector3;

/*
 * An object representation of a game command for MultiCraft
 */
public class GameCommand {
	private String commandName;
	private Player issuer;
	private JSONObject args;
	private MultiCraft plugin;
	
	public GameCommand(JSONObject argsJSON, MultiCraft pl) {
		plugin = pl;
		args = argsJSON;
		commandName = (String) args.get("command");
		issuer = Bukkit.getServer().getPlayer(UUID.fromString(args.get("client_name").toString()));
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
		switch (commandName) {
			case "build":
				return executeBuild();
			case "move":
				return executeMove();
			case "undo":
				return executeUndo();
			case "redo":
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
		
		boolean isHollow = args.containsKey("hollow") && (Boolean) args.get("hollow");
		
		List<BlockRecord> blocksAffected = Commands.buildStructure(l, dimensions, m, isHollow);
		Commands.updateUndoAndRedoStacks(blocksAffected, this.issuer);
		return true;
	}
	
	public static List<BlockRecord>  updateBlocks(Location pos1, Location pos2, Material m) {
		World world = pos1.getWorld();
		int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
		int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
		int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
		int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
		int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
		int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
		
		List<BlockRecord> blocksAffected = new ArrayList<>();

		for (int x = minX; x <= maxX; ++x)
			for (int z = minZ; z <= maxZ; ++z)
				for (int y = minY; y <= maxY; ++y)
					blocksAffected.add(updateBlock(world, x, y, z, m,(byte) 0));

		return blocksAffected;
	}
	
	public void buildHollow(int[] dimensions, Location startLoc, Location endLoc, GameCommand gComm, Material m) {		
		// bottom Wall
		updateBlocks(startLoc, new Location(endLoc.getWorld(), endLoc.getX(), startLoc.getY(), endLoc.getZ()), m);

		// top wall
		updateBlocks(new Location(startLoc.getWorld(), startLoc.getX(), endLoc.getY(), startLoc.getZ()), endLoc, m);
		
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
		Block thisBlock = world.getBlockAt(x, y, z);
		return updateBlock(thisBlock, blockType, blockData);
	}

	private static BlockRecord updateBlock(Block block, Material m, byte blockData) {
		BlockRecord toReturn = new BlockRecord(block.getType(), block.getX(), block.getY(), block.getZ());
		try { block.setType(m); }
		catch(Exception e) {
			// TODO: Handle this mess. At the moment, it updates block async, which raises an error every time. This is an illegal fix :(
			block.getState().update();
		}
		return toReturn;
	}
	
	public boolean executeMove() {
		int distanceToMove = ((Long) args.get("dimensions")).intValue();
		Location pLoc = issuer.getLocation();
		
		
		double rotation = this.issuer.getLocation().getYaw();
		String directionFacedByPlayer = CoordinateCalculations.getSpecificDirection((int) rotation);
		Location newLoc = pLoc.clone();
		
		switch (directionFacedByPlayer) {
		  case ("north") : //negative z
		      newLoc.add(0, 0, distanceToMove * -1);
		      break;
		  case ("east") : // positive x
		      newLoc.add(distanceToMove, 0, 0);
		      break;
		  case ("south") : //positive z
		      newLoc.add(0, 0, distanceToMove);
		      break;
		  case ("west") : //negative x
		      newLoc.add(distanceToMove * -1, 0, 0);
		      break;
		}
		
		this.issuer.sendMessage(CoordinateCalculations.getSpecificDirection((int) rotation));
		Bukkit.getScheduler().runTask(plugin, () -> issuer.teleport(newLoc));

		return true;
	}
	
	public boolean playerIsOnline(Player p) {
		return Bukkit.getOnlinePlayers().contains(p);
	}	
}
