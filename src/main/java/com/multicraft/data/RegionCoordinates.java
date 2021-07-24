package com.multicraft.data;

import org.bukkit.Location;

public class RegionCoordinates {
    private Location startLocation;
    private Location endLocation;

    public RegionCoordinates() {
        startLocation = null;
        endLocation = null;
    }

    public void setStartLocation(Location loc) {
        startLocation = loc;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setEndLocation(Location loc) {
        endLocation = loc;
    }

    public Location getEndLocation() {
        return endLocation;
    }
}
