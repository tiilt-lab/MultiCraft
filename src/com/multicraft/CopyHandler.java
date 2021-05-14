package com.multicraft;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;


public class CopyHandler {

    private HashMap<UUID, Location[]> copyPasteData;

    public CopyHandler() {
        copyPasteData = new HashMap<>();
    }

    public void setCopyLoc1(Player p, Location copyLoc1) {
        copyPasteData.put(p.getUniqueId(), new Location[]{copyLoc1});
    }

    public void setCopyLoc2(Player p, Location copyLoc2) {
        Location[] copyData = copyPasteData.get(p.getUniqueId());
        if (copyData == null)
            p.sendMessage("Please set a first copy location.");
        else
            copyPasteData.put(p.getUniqueId(), new Location[]{copyData[0], copyLoc2});
    }

    public String getCopyArgs(Player p) {
        Location[] copyLocs = copyPasteData.get(p.getUniqueId());
        if(copyLocs == null || copyLocs.length < 2)
            return null;

        return copyLocs[0].getX() + " " + copyLocs[0].getY() + " " + copyLocs[0].getZ() + " "
                + copyLocs[1].getX() + " " + copyLocs[1].getY() + " " + copyLocs[1].getZ() + " ";
    }
}
