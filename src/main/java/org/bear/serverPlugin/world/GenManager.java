package org.bear.serverPlugin.world;

import org.bear.serverPlugin.data.PlayerData;
import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.util.MaterialUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class GenManager {

    private final PluginState state;

    public GenManager(PluginState state) {
        this.state = state;
    }

    // Place generator for a player at a specific location
    public void placeGenForPlayer(Player player, Location location) {
        UUID uuid = player.getUniqueId();
        PlayerData data = state.getPlayerData(uuid);

        // Get the current gen locations or create a new set if none exist
        Set<Location> genLocations = data.getGenLocations();
        if (genLocations == null) {
            genLocations = new HashSet<>();  // Initialize a new set if not present
        }

        // Add new location to genLocations
        genLocations.add(location); // Store multiple gen locations

        // Update genLocations for the player
        data.setGenLocations(genLocations);
        if (state.getPlayerData(player.getUniqueId()).genIsActive) {
            startGenLoop(player);
        }
    }

    private final Map<UUID, BukkitRunnable> genLoopTasks = new HashMap<>();

    // Start the generator loop for a player
    private void startGenLoop(Player player) {
        UUID uuid = player.getUniqueId();

        // Cancel existing task if any
        if (genLoopTasks.containsKey(uuid)) {
            genLoopTasks.get(uuid).cancel();
        }

        long initialDelay = state.getDelayTicks(player); // per-player delay

        BukkitRunnable genTask = new BukkitRunnable() {
            private long lastDelay = initialDelay;

            @Override
            public void run() {
                long currentDelay = state.getDelayTicks(player);
                if (currentDelay != lastDelay) {
                    this.cancel();
                    Bukkit.getScheduler().runTaskLater(
                            Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("ServerPlugin")),
                            () -> startGenLoop(player),
                            1L
                    );
                    return;
                }

                if (!state.getPlayerData(uuid).genIsActive) return;

                // Get all gen locations for the player
                Set<Location> genLocations = state.getPlayerData(uuid).getGenLocations();
                if (genLocations == null || genLocations.isEmpty()) return;

                // Loop over all generator locations
                for (Location loc : genLocations) {
                    Block baseBlock = loc.getBlock();
                    if (baseBlock.getType() != Material.IRON_BLOCK) continue;

                    Block blockAbove = baseBlock.getRelative(BlockFace.UP);
                    if (blockAbove.getType() == Material.AIR) {
                        MaterialUtils genDropMats = new MaterialUtils();
                        Material dropMaterial = genDropMats.getRandomMaterialFromMap(state.valuables); // safer fallback

                        ItemStack item = new ItemStack(dropMaterial);
                        var meta = item.getItemMeta();
                        if (meta != null) {
                            List<String> lore = new ArrayList<>();
                            String description = "Sell price: " + state.sellPrices.getOrDefault(dropMaterial, 0);
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
