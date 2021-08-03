package com.multicraft;

import com.multicraft.data.CommandQueue;
import com.multicraft.net.EchoThread;
import com.multicraft.net.FileThread;
import com.multicraft.net.SimpleSocketServer;
import com.multicraft.net.WebSocketThread;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MultiCraft extends JavaPlugin {

	public String PathToJar = "";
	public String MultiCraftDirName = "";

	private final SimpleSocketServer<EchoThread> speechToTextServer = new SimpleSocketServer<>(this, 5003, EchoThread::new);
	private final SimpleSocketServer<FileThread> fileTransferServer = new SimpleSocketServer<>(this, 5004, FileThread::new);
	private final WebSocketThread webSocketServer = new WebSocketThread(this, 5005);
	private final CommandQueue.CommandExecutor commandExecutor = new CommandQueue.CommandExecutor(this);

	@Override
	public void onEnable() {

		// get jar location and directory for file output
		File filePath = new File(MultiCraftCommandExecutor.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		PathToJar = filePath.getPath().substring(0, filePath.getPath().indexOf(filePath.getName()));
		MultiCraftDirName = PathToJar + File.separator + "MultiCraft";

		// start threads to handle client command and file transfer connections
		speechToTextServer.start();
		fileTransferServer.start();
		webSocketServer.start();
		commandExecutor.start();

		// set up the executor for custom commands
		MultiCraftCommandExecutor mExec = new MultiCraftCommandExecutor(this);

		try {
			getCommand("mbuild").setExecutor(mExec);
			getCommand("mmbuild").setExecutor(mExec);
			getCommand("pyramid").setExecutor(new PyramidBuilder(this));
			getCommand("mundo").setExecutor(mExec);
			getCommand("mredo").setExecutor(mExec);
			getCommand("rloc1").setExecutor(mExec);
			getCommand("rloc2").setExecutor(mExec);
			getCommand("rbuild").setExecutor(mExec);
			getCommand("mstore").setExecutor(mExec);
			getCommand("mclone").setExecutor(mExec);
			getCommand("copyloc1").setExecutor(mExec);
			getCommand("copyloc2").setExecutor(mExec);
			getCommand("mpaste").setExecutor(mExec);
		} catch (NullPointerException ignored) {
			getLogger().severe("MultiCraftCommandExecutor initialization failed! Please ensure plugin.yml is included in JAR and up to date.");
		}

		getLogger().info("MultiCraft has been enabled!");
	}
	
	@Override
	public void onDisable() {
	    // stop threads
		speechToTextServer.interrupt();
		fileTransferServer.interrupt();
		commandExecutor.interrupt();

		getLogger().info("MultiCraft has been disabled.");
	}
	
}
