package org.bear.serverPlugin.world;

import org.bear.serverPlugin.ServerPlugin;
import org.bear.serverPlugin.commands.ChunkIsland;
import org.bear.serverPlugin.commands.ChunkIsland.ChunkCoord;
import org.bear.serverPlugin.data.PluginState;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DecayConcept implements Listener {
    public PluginState state;

    public void decayConcept(PluginState state) {
        this.state = state;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        Chunk chunk = block.getChunk();
        ChunkCoord placedCoord = new ChunkCoord(chunk.getX(), chunk.getZ());

        // ✅ Only skip decay if the block is placed in the player's island chunk
        ChunkCoord playerIsland = ChunkIsland.playerIslandChunks.get(player.getUniqueId());
        if (playerIsland != null && playerIsland.equals(placedCoord)) {
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
        BlockData data = block.getBlockData();

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
