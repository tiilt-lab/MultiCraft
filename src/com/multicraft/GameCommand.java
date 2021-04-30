package com.multicraft;

import java.io.File;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.multicraft.PyramidBuilder.BlockVector3;

/*
 * An object representation of a game command for MultiCraft
 */
public class GameCommand {
	private final MultiCraft plugin;
	private String commandName;
	private Player issuer;
	private JSONObject args;

	public GameCommand(JSONObject argsJSON, MultiCraft plugin) {
		this.plugin = plugin;
		args = argsJSON;
		commandName = (String) args.get("command");
		issuer = Bukkit.getServer().getPlayer(UUID.fromString(args.get("client_name").toString()));

		File filePath = new File(GameCommand.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String jarLocation = filePath.getPath().substring(0, filePath.getPath().indexOf(filePath.getName()));
	}
	
	public GameCommand(MultiCraft plugin) {
		this.plugin = plugin;
	}

	public boolean commandSupported(String com) {
		return CommandWords.getInstance().commands.contains(com);
	}

	public boolean playerIsOnline(Player p) {
		return Bukkit.getOnlinePlayers().contains(p);
	}

	public boolean execute() {
		if(! commandSupported(commandName) || ! playerIsOnline(issuer)) {
			return false;
		}
		switch (commandName) {
			case "build":
				return executeBuild();
			case "place":
				return executePlace();
			case "move":
				return executeMove();
			case "turn":
				return executeTurn();
			case "tilt":
				return executeTilt();
			case "undo":
				return executeUndo();
			case "redo":
				return executeRedo();
			case "store":
				return executeStore();
			case "clone":
				return executeClone();
			case "give":
				return executeGive();
			case "tbuild":
				return executeTBuild();
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public boolean executeBuild() {
		JSONArray dimAr = (JSONArray) args.get("dimensions");
		int[] dimensions = new int[3];
		dimensions[0] = ((Long) dimAr.get(0)).intValue();
		dimensions[1] = ((Long) dimAr.get(1)).intValue();
		dimensions[2] = ((Long) dimAr.get(2)).intValue();
		
		int id = ((Long) args.get("block_code")).intValue();
		Material m = Material.getMaterial(id);

		Location l = issuer.getTargetBlock((HashSet<Byte>) null, 16).getLocation().add(0, 1, 0);

		List<BlockRecord> blocksAffected;
		if (args.containsKey("roof") && (Boolean) args.get("roof")) {
			PyramidBuilder tempBuilder = new PyramidBuilder(plugin);
			blocksAffected = tempBuilder.makePyramid(new BlockVector3(l.getX(), l.getY(), l.getZ()), m, dimensions[0], true, issuer.getWorld());
			Commands.updateUndoAndRedoStacks(blocksAffected, issuer);
			return true;
		}

		boolean isHollow = args.containsKey("hollow") && (Boolean) args.get("hollow");
		blocksAffected = Commands.buildStructure(issuer.getLocation(), l, dimensions, m, isHollow, plugin);
		Commands.updateUndoAndRedoStacks(blocksAffected, issuer);
		return true;
	}

	@SuppressWarnings("deprecation")
	public boolean executePlace() {
		int[] dimensions = {1, 1, 1};
		int id = ((Long) args.get("block_code")).intValue();
		Material m = Material.getMaterial(id);

		Location l = issuer.getTargetBlock((HashSet<Byte>) null, 16).getLocation().add(0, 1, 0);

		List<BlockRecord> blocksAffected = Commands.buildStructure(issuer.getLocation(), l, dimensions, m, false, plugin);
		Commands.updateUndoAndRedoStacks(blocksAffected, issuer);
		return true;
	}

	public boolean executeMove() {
		int distanceToMove = ((Long) args.get("dimensions")).intValue();
		Location pLoc = issuer.getLocation();

		double rotation = pLoc.getYaw();
		String directionFacedByPlayer = CoordinateCalculations.getGeneralDirection((int) rotation);
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
		
		issuer.sendMessage(directionFacedByPlayer);
		issuer.teleport(newLoc);

		return true;
	}

	public boolean executeTurn() {
		String direction = args.get("direction").toString();
		int degrees = ((Long) args.get("dimensions")).intValue();
		Location l = issuer.getLocation();
		if (direction.equalsIgnoreCase("left"))
			l.setYaw(l.getYaw() - degrees);
		else if (direction.equalsIgnoreCase("right"))
			l.setYaw(l.getYaw() + degrees);
		else {
			issuer.sendMessage("Turn moves the camera left and right.");
			return false;
		}

		issuer.teleport(l);
		return true;
	}

	public boolean executeTilt() {
		String direction = args.get("direction").toString();
		int degrees = ((Long) args.get("dimensions")).intValue();
		Location l = issuer.getLocation();
		if (direction.equalsIgnoreCase("down"))
			l.setPitch(l.getPitch() + degrees);
		else if (direction.equalsIgnoreCase("up"))
			l.setPitch(l.getPitch() - degrees);
		else {
			issuer.sendMessage("Tilt moves the camera up and down.");
			return false;
		}

		issuer.teleport(l);
		return true;
	}

	public boolean executeUndo() {
		return Commands.undo(issuer, plugin);
	}

	public boolean executeRedo() {
		return Commands.redo(issuer, plugin);
	}

	public boolean executeStore() {
		return issuer.performCommand("mstore " + args.get("name"));
	}

	public boolean executeClone() {
		return issuer.performCommand("mclone " + args.get("name"));
	}

	@SuppressWarnings("deprecation")
	public boolean executeGive() {
		int id = ((Long) args.get("block_code")).intValue();
		int amount = ((Long) args.get("dimensions")).intValue();

		issuer.getInventory().addItem(new ItemStack(id, amount));
		return true;
	}

	@SuppressWarnings("deprecation")
	public boolean executeTBuild() {
		JSONObject blockMap = (JSONObject) args.get("block_map");

		Location l = issuer.getTargetBlock((HashSet<Byte>) null, 16).getLocation().add(0, 1, 0);

		List<BlockRecord> blocksAffected = Commands.buildTStructure(issuer.getLocation(), l, blockMap, plugin);
		Commands.updateUndoAndRedoStacks(blocksAffected, issuer);
		return true;
	}
}
