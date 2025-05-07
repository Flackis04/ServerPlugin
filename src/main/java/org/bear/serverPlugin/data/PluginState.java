package org.bear.serverPlugin.data;

import org.bear.serverPlugin.ui.*;
import org.bear.serverPlugin.util.MaterialUtils;
import org.bukkit.Material;

import java.util.*;

public class PluginState {

    public int crypto = 0;
    public int delayLevel = 1;

    public ScoreboardManager scoreboardManager;
    public UpgradeUI upgradeUI;
    public PhoneUI phoneUI;
    public SellUI sellUI;
    public CollectionUI collectionUI;
    public final Set<Material> matInCollection = new HashSet<>();
    public final Set<UUID> cooldownPlayers = new HashSet<>();

    public final List<Material> orderedMats = List.of(
            Material.DIRT, Material.STONE, Material.COAL,
            Material.IRON_INGOT, Material.GOLD_INGOT,
            Material.DIAMOND, Material.EMERALD,
            Material.NETHERITE_SCRAP, Material.NETHERITE_INGOT
    );

    public final Map<Material, Double> valuables;
    public final Map<Material, Boolean> seenMaterials;
    public final Map<Material, Integer> sellPrices;
    public final Map<Integer, Integer> delayLevelCosts;

    public PluginState(UpgradeUI upgradeUI, SellUI sellUI, CollectionUI collectionUI, ScoreboardManager scoreboardManager) {
        this.upgradeUI = upgradeUI;
        this.sellUI = sellUI;
        this.collectionUI = collectionUI;
        this.scoreboardManager = scoreboardManager;

        MaterialUtils materialUtils = new MaterialUtils();

        this.valuables = materialUtils.generateExponentialWeights(orderedMats, 0.27);  // Example ratio
        this.sellPrices = materialUtils.generateSellPrices(valuables, 10000);
        this.seenMaterials = materialUtils.generateInitialSeenMap(orderedMats);
        this.delayLevelCosts = generateDelayLevelCosts(); // maxLevel = 16, baseCost = 80
    }

    public float getDelayTicks() {
        return Math.max(20f - (delayLevel - 1), 5f);
    }

    private Map<Integer, Integer> generateDelayLevelCosts() {
        Map<Integer, Integer> delayLevelCosts = new HashMap<>();

        for (int i = 1; i <= 16; i++) {
            // Example logic: cost increases with delay level
            int cost = 80 * i; // This is just a simple example; you can adjust the cost calculation
            delayLevelCosts.put(i, cost);
        }

        return delayLevelCosts;
    }
}
