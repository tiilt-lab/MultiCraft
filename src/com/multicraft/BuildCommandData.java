package com.multicraft;

import com.mysql.fabric.xmlrpc.base.Array;
import jdk.nashorn.internal.ir.Block;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.Arrays;
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

//		for (BlockRecord block : this.blocksAffected) {
//			if (block.x <= start.x && block.y <= start.y && block.z <= start.z) {
//				start = block;
//			}
//			if (block.x >= end.x && block.y >= end.y && block.z >= end.z) {
//				end = block;
//			}
//		}
	}

	public int[] getDimensions() {
		return new int[]
				{ Math.abs(end.x - start.x) + 1,
						Math.abs(end.y - start.y) + 1,
						Math.abs(end.z - start.z) + 1 } ;
	}




}
