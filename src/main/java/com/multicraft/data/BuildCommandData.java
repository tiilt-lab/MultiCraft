package com.multicraft;

import com.multicraft.data.BlockRecord;

import java.util.List;

public class BuildCommandData {
	public int numberOfBlocksAffected;
	public List<BlockRecord> blocksAffected;
	public BlockRecord start;
	public BlockRecord end;
	
	public BuildCommandData(List<BlockRecord> blocks, int num) {
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
