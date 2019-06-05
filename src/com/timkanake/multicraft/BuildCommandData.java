package com.timkanake.multicraft;

import java.util.List;

import org.bukkit.block.Block;

public class BuildCommandData {
	private int numberOfBlocksAffected;
	List<Block> blocksAffected;
	
	public BuildCommandData(List<Block> blocks, int num) {
		this.blocksAffected = blocks;
		this.numberOfBlocksAffected = num;
	}
}
