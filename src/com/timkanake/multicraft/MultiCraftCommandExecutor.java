package com.timkanake.multicraft;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MultiCraftCommandExecutor implements CommandExecutor{
	private final MultiCraft plugin;
	
	public MultiCraftCommandExecutor(MultiCraft plugin) {
		this.plugin = plugin;
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("mbuild")) {
			if(args.length < 3)
				return false;
			int[] dimensions = new int[] {Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])};
			Location playerLoc = p.getLocation();
			int[] buildCoordinates = CoordinateCalculations.getBuildCoordinates(playerLoc, dimensions);
			Location endLoc = new Location(playerLoc.getWorld(), buildCoordinates[0], buildCoordinates[1], buildCoordinates[2]);
			
			Material material = Material.getMaterial(1);
			
			if(args.length > 3) {
				material = Material.getMaterial(Integer.parseInt(args[3]));
			}
			GameCommand gComm = new GameCommand(this.plugin);
			gComm.updateBlocks(playerLoc, endLoc, material);
			return true;
		}
		return false;
	}
	

}
