package com.timkanake.multicraft;

import java.sql.Connection;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.timkanake.multicraft.MySQL;

public class MultiCraft extends JavaPlugin{
	public Connection connection;
	
	@Override
	public void onEnable() {
		getLogger().info("MultiCraft has been enabled");
		PluginManager pm = getServer().getPluginManager();
		
		MySQL.connect();
		
		
		// location tracking listener
		LocationsListener locationsListener = new LocationsListener(this);
		pm.registerEvents(locationsListener, this);
		
		
	}
	
	@Override
	public void onDisable() {
		MySQL.disconnect();
		getLogger().info("MultiCraft has been disabled");
	}
	
}
