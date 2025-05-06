package org.bear.serverPlugin.data;

import org.bear.serverPlugin.ui.PhoneUI;
import org.bear.serverPlugin.ui.ScoreboardManager;
import org.bukkit.Material;

import java.util.*;

public class PluginState {

    public int crypto = 10000;
    public int delayLevel = 1;
    public ScoreboardManager scoreboardManager;
    public PhoneUI phoneUI;

    public final Set<UUID> cooldownPlayers = new HashSet<>();

    public final List<Material> orderedMats = List.of(
            Material.DIRT, Material.STONE, Material.COAL,
            Material.IRON_INGOT, Material.GOLD_INGOT,
            Material.DIAMOND, Material.EMERALD,
            Material.NETHERITE_SCRAP, Material.NETHERITE_INGOT
    );

    public final Map<Material, Double> valuables;

    public PluginState(PhoneUI phoneUI, ScoreboardManager scoreboardManager) {
        this.phoneUI = phoneUI;
        this.scoreboardManager = scoreboardManager;
        this.valuables = generateExponentialWeights(orderedMats);
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
}
