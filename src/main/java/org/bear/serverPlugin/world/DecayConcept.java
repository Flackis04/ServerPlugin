package org.bear.serverPlugin.world;

import org.bear.serverPlugin.ServerPlugin;
import org.bear.serverPlugin.commands.ChunkIsland;
import org.bear.serverPlugin.commands.ChunkIsland.ChunkCoord;
import org.bear.serverPlugin.data.PluginState;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DecayConcept implements Listener {
    private final PluginState state;

    public DecayConcept(PluginState state) {
        this.state = state;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        Chunk chunk = block.getChunk();
        ChunkCoord placedCoord = new ChunkCoord(chunk.getX(), chunk.getZ());
        int chunksLeft = 1-ChunkIsland.usedChunks.size();
        //int chunksLeft = state.getPlayerData(player.getUniqueId()).islandExpansionLevel-ChunkIsland.usedChunks.size();
        if (ChunkIsland.playerIslandChunks.containsValue(placedCoord)) {
            return;
        }

        if (chunksLeft > 0){
            ChunkIsland.usedChunks.add(placedCoord);
            ChunkIsland.playerIslandChunks.put(player.getUniqueId(), placedCoord);
            player.sendMessage("§aYou claimed a chunk!");
            player.sendMessage("Chunksleft: " + (chunksLeft-1) + "/" + 1);
            //player.sendMessage("Chunksleft: " + (chunksLeft-1) + "/" + state.getPlayerData(player.getUniqueId()).islandExpansionLevel);
            return;
        }

        // ✅ Decay everywhere else
        new BukkitRunnable() {
            @Override
            public void run() {
                startDecayAnimation(block);
            }
        }.runTaskLater(ServerPlugin.getPlugin(), 60); // 3 seconds
    }

    private void startDecayAnimation(Block block) {
        Location loc = block.getLocation();
        World world = block.getWorld();

        new BukkitRunnable() {
            int crackStage = 0;

            @Override
            public void run() {
                if (block.getType().isAir()) {
                    cancel();
                    return;
                }

                for (Player player : world.getPlayers()) {
                    if (player.getWorld().equals(world) &&
                            player.getLocation().distance(loc) < 32) { //32 = render
                        player.sendBlockDamage(loc, crackStage / 9f);
                    }
                }

                crackStage++;
                if (crackStage > 9) {
                    block.setType(Material.AIR, false);
                    cancel();
                }
            }
        }.runTaskTimer(ServerPlugin.getPlugin(), 0, 10);
    }
}
