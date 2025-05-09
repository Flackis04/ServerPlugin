package org.bear.serverPlugin.data;

import org.bear.serverPlugin.ui.*;
import org.bear.serverPlugin.util.MaterialUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class PluginState {

    // Shared UI and manager references
    public UpgradeUI upgradeUI;
    public SellUI sellUI;
    public CollectionUI collectionUI;
    public PhoneUI phoneUI;
    public final ScoreboardManager scoreboardManager;

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
    private final Set<Location> ironGenLocations = new HashSet<>();

    public PluginState(
            UpgradeUI upgradeUI,
            SellUI sellUI,
            CollectionUI collectionUI,
            PhoneUI phoneUI,
            ScoreboardManager scoreboardManager
    ) {
        this.upgradeUI = upgradeUI;
        this.sellUI = sellUI;
        this.collectionUI = collectionUI;
        this.phoneUI = phoneUI;
        this.scoreboardManager = scoreboardManager;

        MaterialUtils materialUtils = new MaterialUtils();

        this.valuables = materialUtils.generateExponentialWeights(orderedMats, 0.27);
        this.sellPrices = materialUtils.generateSellPrices(valuables, 10000);
        this.seenMaterials = materialUtils.generateInitialSeenMap(orderedMats);
        this.delayLevelCosts = generateLevelCosts(maxDelayLevel, 120);
        this.slotLevelCosts = generateLevelCosts(maxSlotLevel, 500);
        this.islandExpansionLevelCosts = generateLevelCosts(maxIslandExpansionLevel, 2500);
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, id -> new PlayerData());
    }

    public int getDelayTicks(Player player) {
        int delayLevel = getPlayerData(player.getUniqueId()).delayLevel;
        return Math.max(1, Math.min(15 - (delayLevel * 3), 15));
    }

    private Map<Integer, Integer>
    generateLevelCosts(int maxLevel, int baseCostPerLevel) {
        Map<Integer, Integer> levelCosts = new HashMap<>();

        for (int i = 1; i <= maxLevel; i++) {
            // Example logic: cost increases with delay level
            int cost = baseCostPerLevel * i; // This is just a simple example; you can adjust the cost calculation
            levelCosts.put(i, cost);
        }

        return levelCosts;
    }

    public void placeGenForPlayer(Player player, Location location) {
        UUID uuid = player.getUniqueId();
        PlayerData data = getPlayerData(uuid);

        data.setGenLocation(location); // Save per-player gen location
        data.genIsActive = true;
        startGenLoop(player);
    }

    private final Map<UUID, BukkitRunnable> genLoopTasks = new HashMap<>();

    private void startGenLoop(Player player) {
        UUID uuid = player.getUniqueId();

        // Cancel existing task if any
        if (genLoopTasks.containsKey(uuid)) {
            genLoopTasks.get(uuid).cancel();
        }

        long initialDelay = getDelayTicks(player); // per-player delay

        BukkitRunnable genTask = new BukkitRunnable() {
            private long lastDelay = initialDelay;

            @Override
            public void run() {
                long currentDelay = getDelayTicks(player);
                if (currentDelay != lastDelay) {
                    this.cancel();
                    Bukkit.getScheduler().runTaskLater(
                            Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("ServerPlugin")),
                            () -> startGenLoop(player),
                            1L
                    );
                    return;
                }

                if (!getPlayerData(uuid).genIsActive) return;

                Location loc = getPlayerData(uuid).getGenLocation();
                if (loc == null) return;

                Block baseBlock = loc.getBlock();
                if (baseBlock.getType() != Material.IRON_BLOCK) return;

                Block blockAbove = baseBlock.getRelative(BlockFace.UP);
                if (blockAbove.getType() == Material.AIR) {
                    MaterialUtils genDropMats = new MaterialUtils();
                    Material dropMaterial = genDropMats.getRandomMaterialFromMap(valuables); // safer fallback

                    ItemStack item = new ItemStack(dropMaterial);
                    var meta = item.getItemMeta();
                    if (meta != null) {
                        List<String> lore = new ArrayList<>();
                        String description = "Sell price: " + sellPrices.getOrDefault(dropMaterial, 0);
                        lore.add(description);
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                    }

                    var dropped = blockAbove.getWorld().dropItem(
                            blockAbove.getLocation().add(0.5, 0, 0.5),
                            item
                    );
                    dropped.setVelocity(new Vector(0, 0.45, 0));
                }
            }
        };

        genLoopTasks.put(uuid, genTask);

        genTask.runTaskTimer(
                Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("ServerPlugin")),
                0L,
                initialDelay
        );
    }
}
