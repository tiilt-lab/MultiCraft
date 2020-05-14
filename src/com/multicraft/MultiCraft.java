package com.multicraft;

import org.bukkit.plugin.java.JavaPlugin;

public class MultiCraft extends JavaPlugin{
	private MultiCraftCommandExecutor mExec;

	@Override
	public void onEnable() {
		getLogger().info("MultiCraft has been enabled!");
		// set up the executor for custom commands
		mExec = new MultiCraftCommandExecutor(this);

		this.getCommand("mbuild").setExecutor(mExec);
		this.getCommand("mmbuild").setExecutor(mExec);
		this.getCommand("pyramid").setExecutor(new PyramidBuilder(this));
		this.getCommand("mundo").setExecutor(mExec);
		this.getCommand("mredo").setExecutor(mExec);
		this.getCommand("rbuild").setExecutor(mExec);
		this.getCommand("rloc1").setExecutor(mExec);
		this.getCommand("rloc2").setExecutor(mExec);
		this.getCommand("rrbuild").setExecutor(mExec);
		this.getCommand("eyebuild").setExecutor(mExec);
		this.getCommand("eyetrack").setExecutor(mExec);
		this.getCommand("mstore").setExecutor(mExec);
		this.getCommand("mclone").setExecutor(mExec);
		this.getCommand("copyloc1").setExecutor(mExec);
		this.getCommand("copyloc2").setExecutor(mExec);
		this.getCommand("mpaste").setExecutor(mExec);
		
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
