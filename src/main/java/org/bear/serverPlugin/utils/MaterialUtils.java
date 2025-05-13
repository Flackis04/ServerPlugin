package org.bear.serverPlugin.utils;

import org.bukkit.Material;

import java.util.*;

public class MaterialUtils {

    // Method to generate exponential weights for materials
    public static Map<Material, Double> generateExponentialWeights(List<Material> materials, double ratio) {
        int count = materials.size();
        double[] rawWeights = new double[count];
        double sum = 0.0;

        // Step 1: Compute exponential weights
        for (int i = 0; i < count; i++) {
            rawWeights[i] = Math.pow(ratio, i);
            sum += rawWeights[i];
        }

        // Step 2: Normalize
        for (int i = 0; i < count; i++) {
            rawWeights[i] /= sum;
        }

        // Step 3: Assign to materials
        Map<Material, Double> valueMap = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            valueMap.put(materials.get(i), rawWeights[i]);
        }

        return valueMap;
    }

    // Method to get a random material based on weighted probabilities
    public static Material getRandomMaterialFromMap(Map<Material, Double> weightedMap) {
        double random = Math.random();
        double cumulative = 0.0;

        for (Map.Entry<Material, Double> entry : weightedMap.entrySet()) {
            cumulative += entry.getValue();
            if (random <= cumulative) {
                return entry.getKey();
            }
        }

        // Fallback (in case of rounding error)
        return Material.DIRT;
    }

    // Method to generate initial seen map for materials (e.g., used for tracking if a material has been seen)
    public static Map<Material, Boolean> generateInitialSeenMap(List<Material> materials) {
        Map<Material, Boolean> tracker = new HashMap<>();
        for (Material mat : materials) {
            tracker.put(mat, false);
        }
        return tracker;
    }

    // Method to generate sell prices based on material weights
    public static Map<Material, Integer> generateSellPrices(Map<Material, Double> weights, int baseValue) {
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
}
