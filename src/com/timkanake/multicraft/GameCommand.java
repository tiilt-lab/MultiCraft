package com.timkanake.multicraft;


import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
	
	public void executeBuild() {
		JSONArray dimAr = (JSONArray) args.get("dimensions");
		int[] dimensions = new int[3];
		dimensions[0] = ((Long) dimAr.get(0)).intValue();
		dimensions[1] = ((Long) dimAr.get(1)).intValue();
		dimensions[2] = ((Long) dimAr.get(2)).intValue();
		Location l = issuer.getLocation();
		
		Material m = Material.STONE;
		plugin.getServer().broadcastMessage(((Boolean) (m == null)).toString());
		Block b = l.getBlock();
		updateBlock(b, m, (byte) 0);
		plugin.getServer().broadcastMessage("3");

	}
	
	private void updateBlock(Block block, Material m, byte blockData) {
		try {
			block.setType(m);
		}catch(Exception e) {
			plugin.getServer().broadcastMessage(e.toString());
			plugin.getServer().broadcastMessage("whhaaat!");
		}
		block.setType(m);
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
	
	// updates a block
//	private void updateBlock(World world, Location loc, int blockType, byte blockData) {
//		Block thisBlock = world.getBlockAt(loc);
//		updateBlock(thisBlock, blockType, blockData);
//	}
//	
//	private void updateBlock(World world, int x, int y, int z, int blockType, byte blockData) {
//		Block thisBlock = world.getBlockAt(x,y,z);
//		updateBlock(thisBlock, blockType, blockData);
//	}
//	
//	private void updateBlock(Block block, int blockType, byte blockData) {
//		if ((block.getTypeId() != blockType) || (block.getData() != blockData)) {
//			block.setTypeIdAndData(blockType, blockData, true);
//		}
//	}
	
	
}
