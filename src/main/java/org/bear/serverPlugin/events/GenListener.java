package org.bear.serverPlugin.events;

import org.bear.serverPlugin.data.PluginState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bear.serverPlugin.util.MaterialUtils;

import java.util.*;

public class GenListener implements Listener {
    private final PluginState state;

    // Tracks all placed iron block locations
    private final Set<Location> ironGenLocations = new HashSet<>();

    public GenListener(PluginState state) {
        this.state = state;

        // Start repeating task for generators
        startGenLoop();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlock();

        if (placedBlock.getType() == Material.IRON_BLOCK) {
            Location loc = placedBlock.getLocation();
            ironGenLocations.add(loc);
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack item = event.getItem().getItemStack();
        Material mat = item.getType();
        state.seenMaterials.put(mat, true);  // mark it as seen

// Check if the item has lore and contains "Sell price: "
        boolean hasSellPrice = false;
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasLore()) {
            for (String line : Objects.requireNonNull(meta.getLore())) {
                if (line.contains("Sell price: ")) {
                    hasSellPrice = true;
                    break;
                }
            }
        }

        if (hasSellPrice && state.orderedMats.contains(mat) && state.seenMaterials.getOrDefault(mat, false)) {
            if (!state.matInCollection.contains(mat)) {
                state.matInCollection.add(mat);
                state.collectionUI.createCollectionMat(mat);
                //state.collectionUI.updateCollectionUI(player);
            }
        }

    }


    private BukkitRunnable genLoopTask;

    private void startGenLoop() {
        if (genLoopTask != null) {
            genLoopTask.cancel(); // Cancel existing task if running
        }

        long initialDelay = state.getDelayTicks(); // Store initial delay

        genLoopTask = new BukkitRunnable() {
            private long lastDelay = initialDelay;

            @Override
            public void run() {
                // Check if delay has changed
                long currentDelay = state.getDelayTicks();
                if (currentDelay != lastDelay) {
                    this.cancel(); // Cancel this task
                    Bukkit.getScheduler().runTaskLater(
                            Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("ServerPlugin")),
                            () -> startGenLoop(), 1L // Restart loop with new delay
                    );
                    return;
                }

                if (!state.genIsActive) return;

                for (Location loc : ironGenLocations) {
                    Block baseBlock = loc.getBlock();
                    if (baseBlock.getType() != Material.IRON_BLOCK) continue;

                    Block blockAbove = baseBlock.getRelative(BlockFace.UP);
                    if (blockAbove.getType() == Material.AIR) {
                        MaterialUtils genDropMats = new MaterialUtils();
                        Material dropMaterial = genDropMats.getRandomMaterialFromMap(state.valuables);

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

        genLoopTask.runTaskTimer(
                Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("ServerPlugin")),
                0L,
                initialDelay
        );
    }
}
