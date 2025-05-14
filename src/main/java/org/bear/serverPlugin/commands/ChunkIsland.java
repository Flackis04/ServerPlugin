package org.bear.serverPlugin.commands;

import org.bear.serverPlugin.data.PluginState;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkIsland implements CommandExecutor {

    public static final Set<ChunkCoord> usedChunks = new HashSet<>();
    public static final Map<UUID, ChunkCoord> playerIslandChunks = new ConcurrentHashMap<>();
    private static final Random random = new Random();
    public PluginState state;


    public void chunkIsland(PluginState state){
        this.state = state;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        UUID playerId = player.getUniqueId();

        // Handle "/is remove"
        if (args.length == 1 && args[0].equalsIgnoreCase("remove")) {
            ChunkCoord removed = playerIslandChunks.remove(playerId);
            if (removed != null) {
                usedChunks.remove(removed);
                player.sendMessage(ChatColor.YELLOW + "Your island has been removed.");
            } else {
                player.sendMessage(ChatColor.RED + "You don't have an island to remove.");
            }
            return true;
        }

        // If player already has an island, teleport them to it
        if (playerIslandChunks.containsKey(playerId)) {
            ChunkCoord coord = playerIslandChunks.get(playerId);
            World world = player.getWorld();
            int x = coord.x * 16 + 8;
            int z = coord.z * 16 + 8;
            int y = world.getHighestBlockYAt(x, z) + 1;

            player.teleport(new Location(world, x + 0.5, y, z + 0.5));
            player.sendMessage(ChatColor.AQUA + "Teleported to your island.");
            return true;
        }

        // Otherwise, create a new island
        World world = player.getWorld();
        ChunkCoord newChunk = findSafeChunk();

        usedChunks.add(newChunk);
        playerIslandChunks.put(playerId, newChunk);

        int x = newChunk.x * 16 + 8;
        int z = newChunk.z * 16 + 8;
        int y = random.nextInt(33) + 64;

        for (int dx = -8; dx < 8; dx++) {
            for (int dz = -8; dz < 8; dz++) {
                world.getBlockAt(x + dx, y, z + dz).setType(Material.GRASS_BLOCK);
            }
        }

        player.teleport(new Location(world, x + 0.5, y + 1, z + 0.5));
        player.sendMessage(ChatColor.GREEN + "Welcome to your new island!");

        return true;
    }



    public static ChunkCoord findSafeChunk() {
        World world = Bukkit.getWorlds().get(0); // Assumes the first world (can be changed)
        WorldBorder border = world.getWorldBorder();

        Location center = border.getCenter();
        double size = border.getSize() / 2.0; // Radius from center

        // Add a 2-chunk buffer to the world border
        int buffer = 2;

        int minX = (int) ((center.getX() - size) / 16) + buffer;
        int maxX = (int) ((center.getX() + size) / 16) - buffer;
        int minZ = (int) ((center.getZ() - size) / 16) + buffer;
        int maxZ = (int) ((center.getZ() + size) / 16) - buffer;

        while (true) {
            int cx = random.nextInt(maxX - minX + 1) + minX;
            int cz = random.nextInt(maxZ - minZ + 1) + minZ;

            ChunkCoord test = new ChunkCoord(cx, cz);
            boolean safe = usedChunks.stream().noneMatch(existing ->
                    Math.max(Math.abs(existing.x - cx), Math.abs(existing.z - cz)) < 4
            );

            if (safe) return test;

        }
    }

    public static class ChunkCoord {
        public int x;
        public int z;

        public ChunkCoord(int x, int z) {
            this.x = x;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ChunkCoord)) return false;
            ChunkCoord other = (ChunkCoord) o;
            return x == other.x && z == other.z;
        }

        @Override
        public int hashCode() {
            return 31 * x + z;
        }
    }
}
