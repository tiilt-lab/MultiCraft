package com.timkanake.multicraft;

import org.bukkit.plugin.java.JavaPlugin;

public class MultiCraft extends JavaPlugin{
	
	@Override
	public void onEnable() {
		getLogger().info("MultiCraft has been enabled!");
		// set up the executor for custom commands
		MultiCraftCommandExecutor mExec = new MultiCraftCommandExecutor(this);
		
		this.getCommand("mbuild").setExecutor(mExec);
		this.getCommand("mmbuild").setExecutor(mExec);
		this.getCommand("pyramid").setExecutor(new PyramidBuilder(this));
		this.getCommand("mundo").setExecutor(mExec);
		this.getCommand("mredo").setExecutor(mExec);
		this.getCommand("rbuild").setExecutor(mExec);
		this.getCommand("loc1").setExecutor(mExec);
		this.getCommand("loc2").setExecutor(mExec);
		this.getCommand("rrbuild").setExecutor(mExec);
		
		// start threads to handle client commands
		new SpeechToTextServer(this).start();
		new CommandsListener(this).start();
		new CommandExecution(this).start();
	}
	
	@Override
	public void onDisable() {
		getLogger().info("MultiCraft has been disabled");
	}
	
}
