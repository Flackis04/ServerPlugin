package org.bear.serverPlugin.data;

import org.bear.serverPlugin.ui.*;
import org.bukkit.Material;

import java.util.*;

public class PluginState {

    public int crypto = 10000;
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

        this.valuables = generateExponentialWeights(orderedMats);
        this.sellPrices = generateSellPrices(valuables, 10000);
        this.seenMaterials = generateInitialSeenMap(orderedMats);
        this.delayLevelCosts = generateDelayLevelCosts(16, 80); // maxLevel = 16, baseCost = 80
    }


    public float getDelayTicks() {
        return Math.max(20f - (delayLevel - 1), 5f);
    }

    private Map<Material, Double> generateExponentialWeights(List<Material> materials) {
        int count = materials.size();
        double[] rawWeights = new double[count];
        double sum = 0.0;

        for (int i = 0; i < count; i++) {
            rawWeights[i] = Math.pow(0.27, i);
            sum += rawWeights[i];
        }

        for (int i = 0; i < count; i++) {
            rawWeights[i] /= sum;
        }

        Map<Material, Double> valueMap = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            valueMap.put(materials.get(i), rawWeights[i]);
        }

        return valueMap;
    }

    private Map<Material, Integer> generateSellPrices(Map<Material, Double> weights, int baseValue) {
        Map<Material, Integer> sellPrices = new LinkedHashMap<>();

        // Step 1: Calculate prices
        List<Integer> prices = new ArrayList<>();
        for (double weight : weights.values()) {
            prices.add((int) Math.round(weight * baseValue));
        }

        // Step 2: Sort prices in ascending order
        prices.sort(Comparator.naturalOrder());

        // Step 3: Ensure the smallest value is at least 1
        int minPrice = prices.getFirst();
        if (minPrice < 1) {
            int offset = 1 - minPrice;
            prices.replaceAll(integer -> integer + offset);
        }

        // Step 4: Assign sorted prices to materials in original order
        Iterator<Integer> priceIterator = prices.iterator();
        for (Material material : weights.keySet()) {
            sellPrices.put(material, priceIterator.next());
        }

        return sellPrices;
    }



    private Map<Material, Boolean> generateInitialSeenMap(List<Material> materials) {
        Map<Material, Boolean> tracker = new HashMap<>();
        for (Material mat : materials) {
            tracker.put(mat, false);
        }
        return tracker;
    }

    private Map<Integer, Integer> generateDelayLevelCosts(int maxLevel, int baseCost) {
        Map<Integer, Integer> delayLevelCosts = new HashMap<>();

        for (int i = 1; i <= maxLevel; i++) {
            // Example logic: cost increases with delay level
            int cost = baseCost * i; // This is just a simple example; you can adjust the cost calculation
            delayLevelCosts.put(i, cost);
        }

        return delayLevelCosts;
    }
}
