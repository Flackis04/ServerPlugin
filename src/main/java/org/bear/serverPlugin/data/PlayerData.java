package org.bear.serverPlugin.data;

import org.bukkit.Location;
import org.bukkit.Material;
import java.util.HashSet;
import java.util.Set;

public class PlayerData {
    public int crypto = 0;
    public int delayLevel = 1;
    public int slotLevel = 1;
    public int islandExpansionLevel = 1;
    public boolean genIsActive = false;

    public final Set<Material> matInCollection = new HashSet<>();
    private Location genLocation; // Store it per player


    public void setGenLocation(Location location) {
        this.genLocation = location;
    }
    public Location getGenLocation() {
        return genLocation;
    }
}
