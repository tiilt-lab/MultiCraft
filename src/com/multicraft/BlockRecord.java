package com.multicraft;

import org.bukkit.Material;

public class BlockRecord {
	public Material material;
	public int x;
	public int y;
	public int z;
	
	public BlockRecord(Material m, int x, int y, int z) {
		this.material = m;
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
