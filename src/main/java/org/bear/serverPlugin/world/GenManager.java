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
import java.util.stream.Collectors;

public class GenManager {

    private final PluginState state;

    public GenManager(PluginState state) {
        this.state = state;
    }

    // Place generator for a player at a specific location
    public void placeGenForPlayer(Player player, Location location) {
        UUID uuid = player.getUniqueId();
        PlayerData data = state.getPlayerData(uuid);

        // Update genLocations for the player
        startGenLoop(player);
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

                if (!(state.getPlayerData(uuid).generators.stream().filter(gen -> gen.location != null).count() > 0)) return;

                // Get all gen locations for the player
                Set<Location> genLocations = state.getPlayerData(uuid).generators.stream().map(gen -> gen.location).collect(Collectors.toSet());
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

        if (state.getPlayerData(player.getUniqueId()).generators.stream().anyMatch(gen -> gen.location == block.getLocation())) {
            if (isPickup){
                block.setType(Material.AIR);  // Remove the block from the world
                player.getInventory().addItem(ItemUtils.getGen());  // Give the gen item back
            }

            Location loc = block.getLocation();
            PlayerData playerData = state.getPlayerData(player.getUniqueId());

            boolean removed = playerData.generators.removeIf(gen ->
                    gen.location.getWorld().equals(loc.getWorld()) &&
                            gen.location.getBlockX() == loc.getBlockX() &&
                            gen.location.getBlockY() == loc.getBlockY() &&
                            gen.location.getBlockZ() == loc.getBlockZ());

            if (removed) {
                player.sendMessage("§cRemoved gen. " + playerData.generators.stream().filter(gen -> gen.location != null).count() + "/" + playerData.maxGenerators + " gens placed");
            }
        }
    }

}
