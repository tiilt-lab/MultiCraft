package com.timkanake.multicraft;

import java.util.List;

public class BuildCommandData {
	public int numberOfBlocksAffected;
	List<BlockRecord> blocksAffected;
	
	public BuildCommandData(List<BlockRecord> blocks, int num) {
		this.blocksAffected = blocks;
		this.numberOfBlocksAffected = num;
	}
	
	public BuildCommandData() {
		
	}
}
