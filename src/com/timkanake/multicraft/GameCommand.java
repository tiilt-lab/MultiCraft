package com.timkanake.multicraft;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GameCommand {
	String[] args;
	String commandName;
	Player issuer;
	
	public GameCommand(String str) {
		args = str.split(" ");
		if(args.length > 1) {
			commandName = args[1];
			issuer = getPlayerFromUUID(args[0]);
		}
	}
	
	public boolean commandSupported(String com) {
		return CommandWords.getInstance().commands.contains(com);
	}
	
	public void execute() {
		if(! commandSupported(args[1]) || ! playerIsOnline(issuer)) {
			return;
		}
		if(commandName.equals("build")) {
			executeBuild();
		}
	}
	
	public void executeBuild() {
		String house;
		int materialId;
		for(String s: args) {
			if(Materials.getInstance().materials.containsKey(s)) {
				materialId = Materials.getInstance().getId(s);
				continue;
			}
		}
	}

	public Player getPlayerFromUUID(String uuid) {
		UUID id = UUID.fromString(uuid);
		return (Player) Bukkit.getOfflinePlayer(id);
	}
	
	public boolean playerIsOnline(Player p) {
		return Bukkit.getOnlinePlayers().contains(p);
	}
}
