package com.multicraft;

import com.multicraft.data.RegionCoordinates;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CopyHandler extends RegionSelector {

    private static CopyHandler instance = null;

    public static CopyHandler getInstance() {
        if (instance == null) {
            instance = new CopyHandler();
        }
        return instance;
    }

    private CopyHandler() { }

    public String getCopyArgs(Player p) {
        if (playerRegionSelections.containsKey(p)) {
            RegionCoordinates r = playerRegionSelections.get(p);
            if (r.getStartLocation() != null && r.getStartLocation() != null) {
                Location s = r.getStartLocation();
                Location e = r.getEndLocation();
                return s.getX() + " " + s.getY() + " " + s.getZ() + " " + e.getX() + " " + e.getY() + " " + e.getZ();
            }
        }
        return "";
    }
}
