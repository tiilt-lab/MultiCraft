package com.timkanake.multicraft;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MultiCraft extends JavaPlugin{
	
	@Override
	public void onEnable() {
		getLogger().info("MultiCraft has been enabled");
		PluginManager pm = getServer().getPluginManager();
		
		// location tracking listener
		// LocationsListener locationsListener = new LocationsListener(this);
		// pm.registerEvents(locationsListener, this);
		
		
	}
	
	@Override
	public void onDisable() {
		getLogger().info("MultiCraft has been disabled");
	}
	
}
