package org.bear.serverPlugin.data;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerGenerator {
    public enum Trait {
        DELAY("delay"),
        OVERCLOCK("overclock"),
        SHARD("shard"),
        BEACON("beacon"),
        FORTUNE("fortune");

        public final String columnPrefix;

        Trait(String columnPrefix) {
            this.columnPrefix = columnPrefix;
        }
    }

    public static class TraitData {
        public int level;
        public int maxLevel;
        public int costBase;

        public TraitData(int level, int maxLevel, int costBase) {
            this.level = level;
            this.maxLevel = maxLevel;
            this.costBase = costBase;
        }
    }

    public String name;
    public Block block;

    public Location location;

    public final Map<Trait, TraitData> traits = new HashMap<>();

    public int getDelayTicks() {
        return Math.max(1, 15 - traits.get(Trait.DELAY).level * 3);
    }
}
