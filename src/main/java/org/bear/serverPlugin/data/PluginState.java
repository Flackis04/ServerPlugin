package org.bear.serverPlugin.data;

import org.bear.serverPlugin.ScoreboardManager;
import org.bear.serverPlugin.commands.ChunkIsland;
import org.bear.serverPlugin.ui.*;
import org.bear.serverPlugin.utils.MaterialUtils;
import org.bear.serverPlugin.world.DecayConcept;
import org.bear.serverPlugin.world.GenManager;
import org.bukkit.Material;

import java.util.*;

import org.bukkit.entity.Player;

public class PluginState {

    // Shared UI and manager references
    public UpgradeUI upgradeUI;
    public MarketUI marketUI;
    public SellUI sellUI;
    public CollectionUI collectionUI;
    public PhoneUI phoneUI;
    public GenUI genUI;
    public GenMenuUI genMenuUI;
    public GenManager genManager;
    public DecayConcept decayConcept;
    public final ScoreboardManager scoreboardManager;
    public Database database;


    // Shared item-related data
    public final List<Material> orderedMats = List.of(
            Material.DIRT, Material.STONE, Material.COAL,
            Material.IRON_INGOT, Material.GOLD_INGOT,
            Material.DIAMOND, Material.EMERALD,
            Material.NETHERITE_SCRAP, Material.NETHERITE_INGOT
    );

    public final Map<Material, Double> valuables;
    public final Map<Material, Boolean> seenMaterials;
    public final Map<Material, Integer> sellPrices;

    public final int maxDelayLevel = 4;
    public final int maxSlotLevel = 16;
    public final int maxIslandExpansionLevel = 9;
    public final Map<Integer, Integer> delayLevelCosts;
    public final Map<Integer, Integer> slotLevelCosts;
    public final Map<Integer, Integer> islandExpansionLevelCosts;

    // Per-player data map
    public final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    public PluginState(
            UpgradeUI upgradeUI,
            MarketUI marketUI,
            SellUI sellUI,
            CollectionUI collectionUI,
            PhoneUI phoneUI,
            GenUI genUI,
            GenMenuUI genMenuUI,
            GenManager genManager,
            DecayConcept decayConcept,
            ScoreboardManager scoreboardManager,
            Database database
    ) {
        this.upgradeUI = upgradeUI;
        this.marketUI = marketUI;
        this.sellUI = sellUI;
        this.collectionUI = collectionUI;
        this.phoneUI = phoneUI;
        this.genUI = genUI;
        this.genMenuUI = genMenuUI;
        this.genManager = genManager;
        this.decayConcept = decayConcept;
        this.scoreboardManager = scoreboardManager;
        this.database = database;

        this.valuables = MaterialUtils.generateExponentialWeights(orderedMats, 0.27);
        this.sellPrices = MaterialUtils.generateSellPrices(valuables, 10000);
        this.seenMaterials = MaterialUtils.generateInitialSeenMap(orderedMats);
        this.delayLevelCosts = generateLevelCosts(maxDelayLevel, 1000, 50000, 2.25, true);
        this.slotLevelCosts = generateLevelCosts(maxSlotLevel, 500, 1000000, 2.75, true);
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
        int delayLevel = getPlayerData(player.getUniqueId()).delayLevel;
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
