package com.multicraft;

import com.multicraft.PyramidBuilder.BlockVector3;
import com.multicraft.data.BlockRecord;
import com.multicraft.util.UUIDParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.concurrent.Callable;

import static com.multicraft.Commands.getPlayerTargetLocation;
import static com.multicraft.Commands.updateUndoAndRedoStacks;

/*
 * An object representation of a game command for MultiCraft
 */
public class GameCommand {

	private final MultiCraft plugin;
	private final String commandName;
	private final Player player;
	private final JSONObject args;

	public GameCommand(JSONObject args, MultiCraft plugin) {
		this.plugin = plugin;
		this.args = args;
		commandName = (String) args.get("command");
		player = plugin.getServer().getPlayer(UUIDParser.parse((String) args.get("client_name")));
	}
	
	public boolean execute() {
		if (!player.isOnline()) return false;


		final boolean[] result = {false};
		plugin.getServer().getScheduler().runTask(plugin, () -> {
			switch (commandName) {
				case "build":
					result[0] = executeBuild();
					break;
				case "place":
					result[0] = executePlace();
					break;
				case "move":
					result[0] = executeMove();
					break;
				case "turn":
					result[0] = executeTurn();
					break;
				case "tilt":
					result[0] =  executeTilt();
					break;
				case "undo":
					result[0] = executeUndo();
					break;
				case "redo":
					result[0] = executeRedo();
					break;
				case "store":
					result[0] = executeStore();
					break;
				case "clone":
					result[0] = executeClone();
					break;
				case "give":
					result[0] = executeGive();
					break;
				case "tbuild":
					result[0] = executeTBuild();
					break;
			}
		});

		return result[0];
	}

	public boolean executeBuild() {
		JSONArray dimAr = (JSONArray) args.get("dimensions");
		int[] dimensions = new int[3];
		dimensions[0] = ((Long) dimAr.get(0)).intValue();
		dimensions[1] = ((Long) dimAr.get(1)).intValue();
		dimensions[2] = ((Long) dimAr.get(2)).intValue();
		
		Material m = Commands.getMaterial((String) args.get("material"));

		Location location = getPlayerTargetLocation(player, 16, true);

		List<BlockRecord> blocksAffected;
		if (args.containsKey("roof") && (Boolean) args.get("roof")) {
			PyramidBuilder tempBuilder = new PyramidBuilder(plugin);
			blocksAffected = tempBuilder.makePyramid(new BlockVector3(location.getX(), location.getY(), location.getZ()), m, dimensions[0], true, player.getWorld());
			updateUndoAndRedoStacks(blocksAffected, player);
			return true;
		}

		boolean isHollow = args.containsKey("hollow") && (Boolean) args.get("hollow");
		blocksAffected = Commands.buildStructure(player.getLocation(), location, dimensions, m, isHollow, plugin);
		updateUndoAndRedoStacks(blocksAffected, player);
		return true;
	}

	public boolean executePlace() {
		int[] dimensions = {1, 1, 1};

		Material m = Commands.getMaterial((String) args.get("material"));

		Location location = getPlayerTargetLocation(player, 16, true);

		List<BlockRecord> blocksAffected = Commands.buildStructure(player.getLocation(), location, dimensions, m, false, plugin);
		updateUndoAndRedoStacks(blocksAffected, player);
		return true;
	}

	public boolean executeMove() {
		int distanceToMove = ((Long) args.get("dimensions")).intValue();
		Location pLoc = player.getLocation();

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
		
		teleportPlayer(player, newLoc);
		return true;
	}

	public boolean executeTurn() {
		String direction = args.get("direction").toString();
		int degrees = ((Long) args.get("dimensions")).intValue();
		Location newLoc = player.getLocation();
		if (direction.equalsIgnoreCase("left"))
			newLoc.setYaw(newLoc.getYaw() - degrees);
		else if (direction.equalsIgnoreCase("right"))
			newLoc.setYaw(newLoc.getYaw() + degrees);
		else {
			player.sendMessage("Turn moves the camera left and right.");
			return false;
		}

		teleportPlayer(player, newLoc);
		return true;
	}

	public boolean executeTilt() {
		String direction = args.get("direction").toString();
		int degrees = ((Long) args.get("dimensions")).intValue();
		Location newLoc = player.getLocation();
		if (direction.equalsIgnoreCase("down"))
			newLoc.setPitch(newLoc.getPitch() + degrees);
		else if (direction.equalsIgnoreCase("up"))
			newLoc.setPitch(newLoc.getPitch() - degrees);
		else {
			player.sendMessage("Tilt moves the camera up and down.");
			return false;
		}

		teleportPlayer(player, newLoc);
		return true;
	}

	public boolean executeUndo() {
		return Commands.undo(player, plugin);
	}

	public boolean executeRedo() {
		return Commands.redo(player, plugin);
	}

	public boolean executeStore() {
		return player.performCommand("mstore " + args.get("name"));
	}

	public boolean executeClone() {
		return player.performCommand("mclone " + args.get("name"));
	}

	public boolean executeGive() {
		Material material = Commands.getMaterial((String) args.get("material"));
		int amount = ((Long) args.get("dimensions")).intValue();

		player.getInventory().addItem(new ItemStack(material, amount));
		return true;
	}

	public boolean executeTBuild() {
		JSONObject blockMap = (JSONObject) args.get("block_map");

		Location location = Commands.getPlayerTargetLocation(player, 16, true);

		List<BlockRecord> blocksAffected = Commands.buildTStructure(player.getLocation(), location, blockMap, plugin);
		updateUndoAndRedoStacks(blocksAffected, player);
		return true;
	}

	private void teleportPlayer(Player p, Location l) {
		Bukkit.getScheduler().runTask(plugin, () -> p.teleport(l));
	}

}
