package com.timkanake.multicraft;


import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.timkanake.multicraft.CoordinateCalculations;

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
	
	public void execute() {
		if(! commandSupported(commandName) || ! playerIsOnline(issuer)) {
			return;
		}
		if(commandName.equals("build")) {
			executeBuild();
		}else if(commandName.equals("move")) {
			executeMove();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void executeBuild() {
		JSONArray dimAr = (JSONArray) args.get("dimensions");
		int[] dimensions = new int[3];
		dimensions[0] = ((Long) dimAr.get(0)).intValue();
		dimensions[1] = ((Long) dimAr.get(1)).intValue();
		dimensions[2] = ((Long) dimAr.get(2)).intValue();
		Location l = issuer.getLocation();
		
		int[] buildCoordinates = CoordinateCalculations.getBuildCoordinates(l, dimensions);
		Location l2 = new Location(l.getWorld(), buildCoordinates[0], buildCoordinates[1], buildCoordinates[2]);
		
		int id = ((Long) args.get("block_code")).intValue();
		Material m = Material.getMaterial(id);
		updateBlocks(l, l2, m);
	}
	
	public void  updateBlocks(Location pos1, Location pos2, Material m) {
		int minX, maxX, minY, maxY, minZ, maxZ;
		World world = pos1.getWorld();
		minX = pos1.getBlockX() < pos2.getBlockX() ? pos1.getBlockX() : pos2.getBlockX();
		maxX = pos1.getBlockX() >= pos2.getBlockX() ? pos1.getBlockX() : pos2.getBlockX();
		minY = pos1.getBlockY() < pos2.getBlockY() ? pos1.getBlockY() : pos2.getBlockY();
		maxY = pos1.getBlockY() >= pos2.getBlockY() ? pos1.getBlockY() : pos2.getBlockY();
		minZ = pos1.getBlockZ() < pos2.getBlockZ() ? pos1.getBlockZ() : pos2.getBlockZ();
		maxZ = pos1.getBlockZ() >= pos2.getBlockZ() ? pos1.getBlockZ() : pos2.getBlockZ();

		for (int x = minX; x <= maxX; ++x) {
			for (int z = minZ; z <= maxZ; ++z) {
				for (int y = minY; y <= maxY; ++y) {
					updateBlock(world, x, y, z, m,(byte) 0);
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
	
	public void executeMove() {
		int distanceToMove = ((Long) args.get("dimensions")).intValue();
		Location pLoc = issuer.getLocation();
		Location newLoc = pLoc.add(distanceToMove, 0, 0);
		issuer.teleport(newLoc);
	}

	public Player getPlayerFromUUID(String uuid) {
		UUID id = UUID.fromString(uuid);
		return (Player) Bukkit.getOfflinePlayer(id);
	}
	
	public boolean playerIsOnline(Player p) {
		return Bukkit.getOnlinePlayers().contains(p);
	}	
}
