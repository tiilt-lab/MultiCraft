package com.multicraft.data;

import org.bukkit.Material;

import java.util.Objects;

public class BlockRecord {

	public final Material m;
	public final int x;
	public final int y;
	public final int z;
	
	public BlockRecord(Material m, int x, int y, int z) {
		this.m = m;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof BlockRecord)) {
			return false;
		}

		BlockRecord c = (BlockRecord) o;
		return this.m == c.m && this.x == c.x && this.y == c.y && this.z == c.z;
	}

	@Override
	public int hashCode() {
		return Objects.hash(m, x, y, z);
	}

	@Override
	public String toString() {
		return String.format("material: %s, x: %d, y: %d, z: %d", m, x, y, z);
	}

}
