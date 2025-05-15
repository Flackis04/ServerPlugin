package org.bear.serverPlugin.data;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public class PlayerData {

    public int crypto;
    public int maxGenerators;
    public int multiplier;

    public final Set<Island> islands = new HashSet<>();
    public final Set<PlayerGenerator> generators = new HashSet<>();
    public final Set<Material> seenMaterials = new HashSet<>();
}
