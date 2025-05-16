package org.bear.serverPlugin.data;

import org.bear.serverPlugin.ScoreboardManager;
import org.bear.serverPlugin.utils.MaterialUtils;
import org.bukkit.Material;

import java.util.*;

public class PluginState {
    public final ScoreboardManager scoreboardManager;
    public Database database;



    // Shared item-related data
    public final List<Material> orderedMats = List.of(
            Material.DIRT, Material.STONE, Material.COAL,
            Material.IRON_INGOT, Material.GOLD_INGOT,
            Material.DIAMOND, Material.EMERALD,
            Material.NETHERITE_SCRAP, Material.NETHERITE_INGOT
    );
    public Map<Material, Double> valuables = MaterialUtils.generateExponentialWeights(orderedMats, 0.27, 1);
//    public Map<Material, Double> valuables = MaterialUtils.generateExponentialWeights(orderedMats, 0.27, multiplierLevel);

    public Map<Material, Integer> sellPrices = MaterialUtils.generateSellPrices(this.valuables, 10000);


    public final int maxDelayLevel = 4;
    public final int maxSlotLevel = 16;
    public final int maxIslandExpansionLevel = 9;
    public final Map<Integer, Integer> islandExpansionLevelCosts;

    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    public final Map<Integer, List<PlayerGenerator>> playerGenerators;

    public PluginState(ScoreboardManager scoreboardManager, Database database) {
        this.scoreboardManager = scoreboardManager;
        this.database = database;

        //this.delayLevelCosts = generateLevelCosts(maxDelayLevel, 1000, 50000, 2.25, true);
        //this.slotLevelCosts = generateLevelCosts(maxSlotLevel, 500, 1000000, 2.75, true);
        this.islandExpansionLevelCosts = generateLevelCosts(maxIslandExpansionLevel, 10000, 5000000, 3.25, true);
    }

    public PlayerData getPlayerData(UUID uuid) {
        PlayerData playerData = playerDataMap.get(uuid);
        if(playerData==null){
            database.insertPlayerData(uuid.hashCode());
            playerData = database.loadPlayerData(uuid.hashCode());
            playerDataMap.put(uuid, playerData);
        }
        return playerData;
    }

    public int getDelayTicks(Player player) {
        //int delayLevel = getPlayerData(player.getUniqueId()).delayLevel;
        int delayLevel = 1;
        return Math.max(1, Math.min(15 - (delayLevel * 3), 15));
    }

    public Map<Integer, Integer> generateLevelCosts(int maxLevel, int minCost, int maxCost, double factor, boolean isGrowth) {
        Map<Integer, Integer> levelCosts = new HashMap<>();

        for (int i = 1; i <= maxLevel; i++) {
            double progress = (double)(i - 1) / (maxLevel - 1);
            double scaledProgress = isGrowth ? Math.pow(progress, factor) : 1 - Math.pow(1 - progress, factor);
            int cost = (int)(minCost + (maxCost - minCost) * scaledProgress);
            levelCosts.put(i, cost);
        }

        return levelCosts;
    }

}
