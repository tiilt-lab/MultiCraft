package com.multicraft;

import org.bukkit.Material;

import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof BlockRecord)) {
			return false;
		}

		BlockRecord c = (BlockRecord) o;

		return this.material == c.material && this.x == c.x && this.y == c.y && this.z == c.z;
	}

	@Override
	public int hashCode() {
		return Objects.hash(material, x, y, z);
	}

	@Override
	public String toString() {
		return "Material: " + material + ", x: " + x + ", y: " + y + ", z: " + z;
	}
}
