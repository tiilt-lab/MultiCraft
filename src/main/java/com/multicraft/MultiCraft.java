package com.multicraft;

import com.multicraft.data.CommandQueues;
import com.multicraft.net.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MultiCraft extends JavaPlugin {

	public String PathToJar = "";
	public String MultiCraftDirName = "";

	private final SimpleSocketServer<EchoThread> speechToTextServer = new SimpleSocketServer<>(this, 5003, EchoThread::new);
	private final SimpleSocketServer<FileThread> fileTransferServer = new SimpleSocketServer<>(this, 5004, FileThread::new);
	private final MCWebSocketServer webSocketServer = new MCWebSocketServer(this, 5005);
	private final CommandQueues.CommandListener commandListener = new CommandQueues.CommandListener(this);
	private final CommandQueues.CommandExecutor commandExecutor = new CommandQueues.CommandExecutor(this);

	@Override
	public void onEnable() {
		getLogger().info("MultiCraft has been enabled!");
		// get jar location and directory for file output
		File filePath = new File(MultiCraftCommandExecutor.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		PathToJar = filePath.getPath().substring(0, filePath.getPath().indexOf(filePath.getName()));
		MultiCraftDirName = PathToJar + File.separator + "MultiCraft";

		// set up the executor for custom commands
		MultiCraftCommandExecutor mExec = new MultiCraftCommandExecutor(this);

		getCommand("mbuild").setExecutor(mExec);
		getCommand("mmbuild").setExecutor(mExec);
		getCommand("pyramid").setExecutor(new PyramidBuilder(this));
		getCommand("mundo").setExecutor(mExec);
		getCommand("mredo").setExecutor(mExec);
		getCommand("rbuild").setExecutor(mExec);
		getCommand("rloc1").setExecutor(mExec);
		getCommand("rloc2").setExecutor(mExec);
		getCommand("rrbuild").setExecutor(mExec);
		getCommand("mstore").setExecutor(mExec);
		getCommand("mclone").setExecutor(mExec);
		getCommand("copyloc1").setExecutor(mExec);
		getCommand("copyloc2").setExecutor(mExec);
		getCommand("mpaste").setExecutor(mExec);
		
		// start threads to handle client command and file transfer connections
		speechToTextServer.start();
		fileTransferServer.start();
		webSocketServer.start();
		commandListener.start();
		commandExecutor.start();
	}
	
	@Override
	public void onDisable() {
	    // stop threads
		speechToTextServer.interrupt();
		fileTransferServer.interrupt();
		commandListener.interrupt();
		commandExecutor.interrupt();

		getLogger().info("MultiCraft has been disabled.");
	}
	
}
