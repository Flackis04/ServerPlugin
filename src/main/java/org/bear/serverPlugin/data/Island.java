package org.bear.serverPlugin.data;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class Island {
    public Player player;
    public Set<Location> chunkLocations = new HashSet<>();
    public int expansionLevel;
}
