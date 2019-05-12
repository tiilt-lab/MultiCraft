package com.timkanake.multicraft;

import java.sql.Connection;

// import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MultiCraft extends JavaPlugin{
	public Connection connection;
	
	@Override
	public void onEnable() {
		getLogger().info("MultiCraft without database has been enabled!");
		// PluginManager pm = getServer().getPluginManager();
		this.getCommand("mbuild").setExecutor(new MultiCraftCommandExecutor(this));
		this.getCommand("pyramid").setExecutor(new PyramidBuilder(this));
		this.getCommand("ray").setExecutor(new RayTracingTest(this));
		
//		try {
//			MySQL.connect();
//			
//			InventoryListener inventoryListener = new InventoryListener(this);
//			pm.registerEvents(inventoryListener,  this);
//			
//			LocationsListener locationsListener = new LocationsListener(this);
//			pm.registerEvents(locationsListener, this);
//			
//			BlockEventsListener blockPlacementListener = new BlockEventsListener(this);
//			pm.registerEvents(blockPlacementListener, this);
//		}catch(Exception e) {
//			//do nothing
//		}
		
//		new SpeechToTextServer(this).start();
//		new CommandsListener(this).start();
//		new CommandExecution(this).start();
	}
	
	@Override
	public void onDisable() {
		
//		MySQL.disconnect();
		getLogger().info("MultiCraft has been disabled");
	}
	
}
