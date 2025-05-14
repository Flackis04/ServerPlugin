package org.bear.serverPlugin.world;

import org.bear.serverPlugin.ServerPlugin;
import org.bear.serverPlugin.data.PlayerData;
import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.utils.GenUtils;
import org.bear.serverPlugin.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
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
        if (!state.getPlayerData(player.getUniqueId()).genIsActive) {
            state.getPlayerData(player.getUniqueId()).genIsActive = true;
            startGenLoop(player);
        }
    }

    private final Map<UUID, BukkitRunnable> genLoopTasks = new HashMap<>();

    // Start the generator loop for a player
    public void startGenLoop(Player player) {
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
                            ServerPlugin.getPlugin(),
                            () -> startGenLoop(player),
                            1L
                    );
                    return;
                }

                if (!state.getPlayerData(uuid).genIsActive) return;
                if (!(state.getPlayerData(uuid).gensPlaced > 0)) return;
                state.getPlayerData(uuid).genIsActive = true;

                // Get all gen locations for the player
                Set<Location> genLocations = state.getPlayerData(uuid).getGenLocations();
                if (genLocations == null || genLocations.isEmpty()) return;

                // Loop over all generator locations
                for (Location loc : genLocations) {
                    Block baseBlock = loc.getBlock();
                    if (baseBlock.getType() != Material.IRON_BLOCK) continue;

                    Block blockAbove = baseBlock.getRelative(BlockFace.UP);
                    if (blockAbove.getType() == Material.AIR) {


                        var dropped = blockAbove.getWorld().dropItem(
                                blockAbove.getLocation().add(0.5, 0, 0.5),
                                GenUtils.randomValuable(state, player)
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

    public void onGenRemoveEvent(Player player, Block block, Boolean isPickup) {

        if (state.getPlayerData(player.getUniqueId()).getGenLocations().contains(block.getLocation())) {
            if (isPickup){
                block.setType(Material.AIR);  // Remove the block from the world
                player.getInventory().addItem(ItemUtils.getGen());  // Give the gen item back
            }

            Location loc = block.getLocation();
            PlayerData playerData = state.getPlayerData(player.getUniqueId());
            Set<Location> genLocations = playerData.getGenLocations();

            boolean removed = genLocations.removeIf(existingLoc ->
                    existingLoc.getWorld().equals(loc.getWorld()) &&
                            existingLoc.getBlockX() == loc.getBlockX() &&
                            existingLoc.getBlockY() == loc.getBlockY() &&
                            existingLoc.getBlockZ() == loc.getBlockZ()
            );

            if (removed) {
                playerData.setGenLocations(genLocations);
                playerData.gensPlaced -= 1;
                if (playerData.gensPlaced == 0) playerData.genIsActive = false;
                player.sendMessage("§cRemoved gen. " + playerData.gensPlaced + "/" + playerData.slotLevel + " gens placed");
            }
        }
    }

}
