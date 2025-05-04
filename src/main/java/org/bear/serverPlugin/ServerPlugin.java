package org.bear.serverPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.scheduler.BukkitRunnable;

public class ServerPlugin extends JavaPlugin implements Listener {

    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("ServerPlugin enabled on Minecraft 1.21");
        getCommand("phone").setExecutor(this); // Register the /phone command

        // Start a repeating task that checks the player's held item
        startItemCheckTask();
    }

    public void onDisable() {
        getLogger().info("ServerPlugin disabled");
    }

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            player.sendMessage("§aWelcome!");
        }
    }

    private void startItemCheckTask() {
        // Create a repeating task that checks every 20 ticks (1 second)
        new BukkitRunnable() {
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ItemStack itemInHand = player.getInventory().getItemInMainHand();

                    // Check if the item is not null and has metadata with custom model data
                    if (itemInHand.getItemMeta() != null && itemInHand.getItemMeta().hasCustomModelData()) {
                        player.sendMessage("§aYou are holding the custom model item!");
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20L); // Delay: 0 ticks, Period: 20 ticks (1 second)
    }
}
