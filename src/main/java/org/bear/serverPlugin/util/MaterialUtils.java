package org.bear.serverPlugin.util;

import org.bukkit.Material;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MaterialUtils {
    private Material getRandomMaterialFromMap(Map<Material, Double> weightedMap) {
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

    public Map<Material, Double> generateExponentialWeights(List<Material> materials, double ratio) {
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
}
