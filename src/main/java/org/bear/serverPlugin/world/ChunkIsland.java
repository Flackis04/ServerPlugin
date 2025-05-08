package org.bear.serverPlugin.world;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ChunkIsland implements CommandExecutor {

    private final Set<ChunkCoord> usedChunks = new HashSet<>();
    private final Random random = new Random();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        World world = player.getWorld(); // Can be customized
        ChunkCoord newChunk = findSafeChunk();

        usedChunks.add(newChunk);
        int x = newChunk.x * 16 + 8;
        int z = newChunk.z * 16 + 8;
        int y = random.nextInt(33) + 64;  // Random Y between 64 and 96

        // Create 16x16 platform
        for (int dx = -8; dx < 8; dx++) {
            for (int dz = -8; dz < 8; dz++) {
                world.getBlockAt(x + dx, y, z + dz).setType(Material.GRASS_BLOCK);
            }
        }

        // Teleport player
        player.teleport(new Location(world, x + 0.5, y + 1, z + 0.5));
        player.sendMessage(ChatColor.GREEN + "Welcome to your new island!");

        return true;
    }

    private ChunkCoord findSafeChunk() {
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

    private static class ChunkCoord {
        int x, z;

        ChunkCoord(int x, int z) {
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
