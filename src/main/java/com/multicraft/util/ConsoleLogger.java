package com.multicraft.util;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class ConsoleLogging {

	static ConsoleCommandSender console = Bukkit.getConsoleSender();
	
	public static void printToConsole(String s) {
		console.sendMessage("\247c[\2476MultiCraft\247c] \247b " + s);		
	}

}
