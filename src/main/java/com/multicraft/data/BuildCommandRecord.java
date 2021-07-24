package com.multicraft.data;

import java.util.List;

public class BuildCommandRecord {

	public int numberOfBlocksAffected;
	public List<BlockRecord> blocksAffected;
	public BlockRecord start;
	public BlockRecord end;
	
	public BuildCommandRecord(List<BlockRecord> blocks, int num) {
		this.blocksAffected = blocks;
		this.numberOfBlocksAffected = num;
		setLocations();
	}

	private void setLocations() {
		start = blocksAffected.get(0);
		end = blocksAffected.get(blocksAffected.size() - 1);
	}

	public int[] getDimensions() {
		return new int[]{Math.abs(end.x - start.x) + 1, Math.abs(end.y - start.y) + 1, Math.abs(end.z - start.z) + 1};
	}

}
