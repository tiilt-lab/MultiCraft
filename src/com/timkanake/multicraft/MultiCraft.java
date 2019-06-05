package com.timkanake.multicraft;

import java.sql.Connection;

import org.bukkit.plugin.java.JavaPlugin;

public class MultiCraft extends JavaPlugin{
	public Connection connection;
	
	@Override
	public void onEnable() {
		getLogger().info("MultiCraft without database has been enabled!");
		MultiCraftCommandExecutor mExec = new MultiCraftCommandExecutor(this);
		this.getCommand("mbuild").setExecutor(mExec);
		this.getCommand("pyramid").setExecutor(new PyramidBuilder(this));
		this.getCommand("ray").setExecutor(new RayTracingTest(this));
		this.getCommand("mundo").setExecutor(mExec);
		
		new SpeechToTextServer(this).start();
		new CommandsListener(this).start();
		new CommandExecution(this).start();
	}
	
	@Override
	public void onDisable() {
		getLogger().info("MultiCraft has been disabled");
	}
	
}
