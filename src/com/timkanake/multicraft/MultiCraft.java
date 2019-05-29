package com.timkanake.multicraft;

import java.sql.Connection;

import org.bukkit.plugin.java.JavaPlugin;

public class MultiCraft extends JavaPlugin{
	public Connection connection;
	
	@Override
	public void onEnable() {
		getLogger().info("MultiCraft without database has been enabled!");
		this.getCommand("mbuild").setExecutor(new MultiCraftCommandExecutor(this));
		this.getCommand("pyramid").setExecutor(new PyramidBuilder(this));
		this.getCommand("ray").setExecutor(new RayTracingTest(this));
		
		new SpeechToTextServer(this).start();
		new CommandsListener(this).start();
		new CommandExecution(this).start();
	}
	
	@Override
	public void onDisable() {
		getLogger().info("MultiCraft has been disabled");
	}
	
}
