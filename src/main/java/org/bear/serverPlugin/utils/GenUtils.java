package org.bear.serverPlugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class GenUtils {
    public static String serializeLocations(Set<Location> locations) {
        StringBuilder sb = new StringBuilder();
        for (Location loc : locations) {
            sb.append(loc.getWorld().getName())
                    .append(",").append(loc.getX())
                    .append(",").append(loc.getY())
                    .append(",").append(loc.getZ())
                    .append(";");
        }
        return sb.toString();
    }

    public static Set<Location> deserializeLocations(String serialized) {
        Set<Location> locations = new HashSet<>();
        if (serialized == null || serialized.isEmpty()) return locations;

        String[] locEntries = serialized.split(";");
        for (String entry : locEntries) {
            try {
                String[] parts = entry.split(",");
                if (parts.length != 4) continue;
                String world = parts[0];
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                double z = Double.parseDouble(parts[3]);
                locations.add(new Location(Bukkit.getWorld(world), x, y, z));
            } catch (Exception e) {
                System.err.println("Failed to parse location: " + entry + " Error: " + e.getMessage());
            }
        }
        return locations;
    }
}
