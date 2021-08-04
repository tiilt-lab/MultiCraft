package com.multicraft;

import com.multicraft.PyramidBuilder.BlockVector3;
import com.multicraft.data.BlockRecord;
import com.multicraft.util.UUIDParser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

import static com.multicraft.Commands.getPlayerTargetLocation;
import static com.multicraft.Commands.updateUndoAndRedoStacks;

/*
 * An object representation of a game command for MultiCraft
 */
public class GameCommand {

	private final MultiCraft plugin;
	private final String commandName;
	private final Player issuer;
	private final JSONObject args;

	public GameCommand(JSONObject args, MultiCraft plugin) {
		this.plugin = plugin;
		this.args = args;
		commandName = (String) args.get("command");
		issuer = plugin.getServer().getPlayer(UUIDParser.parse((String) args.get("client_name")));
	}
	
	public boolean execute() {
		if (!issuer.isOnline()) return false;

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

	public boolean executeBuild() {
		JSONArray dimAr = (JSONArray) args.get("dimensions");
		int[] dimensions = new int[3];
		dimensions[0] = ((Long) dimAr.get(0)).intValue();
		dimensions[1] = ((Long) dimAr.get(1)).intValue();
		dimensions[2] = ((Long) dimAr.get(2)).intValue();
		
		Material m = Commands.getMaterial((String) args.get("material"));

		Location location = getPlayerTargetLocation(issuer, 16, true);

		List<BlockRecord> blocksAffected;
		if (args.containsKey("roof") && (Boolean) args.get("roof")) {
			PyramidBuilder tempBuilder = new PyramidBuilder(plugin);
			blocksAffected = tempBuilder.makePyramid(new BlockVector3(location.getX(), location.getY(), location.getZ()), m, dimensions[0], true, issuer.getWorld());
			updateUndoAndRedoStacks(blocksAffected, issuer);
			return true;
		}

		boolean isHollow = args.containsKey("hollow") && (Boolean) args.get("hollow");
		blocksAffected = Commands.buildStructure(issuer.getLocation(), location, dimensions, m, isHollow, plugin);
		updateUndoAndRedoStacks(blocksAffected, issuer);
		return true;
	}

	public boolean executePlace() {
		int[] dimensions = {1, 1, 1};

		Material m = Commands.getMaterial((String) args.get("material"));

		Location location = getPlayerTargetLocation(issuer, 16, true);

		List<BlockRecord> blocksAffected = Commands.buildStructure(issuer.getLocation(), location, dimensions, m, false, plugin);
		updateUndoAndRedoStacks(blocksAffected, issuer);
		return true;
	}

	public boolean executeMove() {
		int distanceToMove = ((Long) args.get("dimensions")).intValue();
		Location pLoc = issuer.getLocation();

		double rotation = pLoc.getYaw();
		String directionFacedByPlayer = CoordinatesCalculator.getGeneralDirection((int) rotation);
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

	public boolean executeGive() {
		Material material = Commands.getMaterial((String) args.get("material"));
		int amount = ((Long) args.get("dimensions")).intValue();

		issuer.getInventory().addItem(new ItemStack(material, amount));
		return true;
	}

	public boolean executeTBuild() {
		JSONObject blockMap = (JSONObject) args.get("block_map");

		Location location = Commands.getPlayerTargetLocation(issuer, 16, true);

		List<BlockRecord> blocksAffected = Commands.buildTStructure(issuer.getLocation(), location, blockMap, plugin);
		updateUndoAndRedoStacks(blocksAffected, issuer);
		return true;
	}

}
