package com.timkanake.multicraft;

import java.sql.Connection;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MultiCraft extends JavaPlugin{
	public Connection connection;
	
	@Override
	public void onEnable() {
		getLogger().info("MultiCraft has been enabled");
		@SuppressWarnings("unused")
		PluginManager pm = getServer().getPluginManager();
		
		// MySQL.connect();
		
		// location tracking listener
//		 LocationsListener locationsListener = new LocationsListener(this);
//		 pm.registerEvents(locationsListener, this);
		new SpeechToTextServer(this).start();
		new CommandsListener(this).start();
		new CommandExecution(this).start();
	}
	
	@Override
	public void onDisable() {
		// MySQL.disconnect();
		getLogger().info("MultiCraft has been disabled");
	}
	
}
