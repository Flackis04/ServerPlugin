package org.bear.serverPlugin.world;

import org.bear.serverPlugin.ServerPlugin;
import org.bear.serverPlugin.data.PlayerData;
import org.bear.serverPlugin.data.PlayerGenerator;
import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.utils.GenUtils;
import org.bear.serverPlugin.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GeneratorTask {
    public UUID playerUuid;
    public PlayerGenerator generator;
    public BukkitRunnable task;

    private static final List<GeneratorTask> generatorTasks = new LinkedList<>();

    public GeneratorTask(UUID playerUuid, PlayerGenerator generator) {
        this.playerUuid = playerUuid;
        this.generator = generator;

        task = new BukkitRunnable() {
            @Override
            public void run() {
                // TODO: restart generator tasks on upgrade

                Block dropAt = generator.location.getBlock().getRelative(BlockFace.UP);

                if (dropAt.getType() == Material.AIR)
                    dropAt.getWorld().dropItem(
                            dropAt.getLocation().add(0.5, 0, 0.5),
                            GenUtils.randomValuable()
                    ).setVelocity(new Vector(0, 0.45, 0));
            }
        };

        task.runTaskTimer(ServerPlugin.getPlugin(), 0L, generator.getDelayTicks());

        generatorTasks.add(this);
    }

    public static void onGenRemoveEvent(PluginState state, Player player, Block block) {
        var generatorAtBlock = state.playerGenerators.values().stream()
                .flatMap(Collection::stream).collect(Collectors.toSet()).stream()
                .filter(g -> g.location.equals(block.getLocation()))
                .findAny();

        if (generatorAtBlock.isEmpty()) return;

        block.setType(Material.AIR);

        Location loc = block.getLocation();
        PlayerData playerData = state.getPlayerData(player.getUniqueId());

        boolean removed = playerData.generators.removeIf(gen ->
                gen.location.getWorld().equals(loc.getWorld()) &&
                        gen.location.getBlockX() == loc.getBlockX() &&
                        gen.location.getBlockY() == loc.getBlockY() &&
                        gen.location.getBlockZ() == loc.getBlockZ());

        if (removed)
            player.sendMessage("§cRemoved gen. " + playerData.generators.stream().filter(gen -> gen.location != null).count() + "/" + playerData.maxGenerators + " gens placed");
    }
}