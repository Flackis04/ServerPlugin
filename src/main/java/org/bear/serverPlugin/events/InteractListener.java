package org.bear.serverPlugin.events;

import org.bear.serverPlugin.data.PluginState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class InteractListener implements Listener {

    private final PluginState state;

    public InteractListener(PluginState state) {
        this.state = state;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != RIGHT_CLICK_AIR && event.getAction() != RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Check cooldown
        if (state.cooldownPlayers.contains(uuid)) {
            player.sendMessage("§cPlease wait before using that again!");
            return;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = itemInHand.getItemMeta();

        if (itemMeta != null && itemMeta.hasCustomModelData()) {
            player.sendMessage("§aYou right-clicked with a custom model item!");

            // Give random material from weighted valuables map
            Material mat = getRandomMaterialFromMap(state.valuables);
            player.getInventory().addItem(new ItemStack(mat));

            // Add to cooldown and schedule removal
            state.cooldownPlayers.add(uuid);
            Bukkit.getScheduler().runTaskLater(
                    Bukkit.getPluginManager().getPlugin("ServerPlugin"),
                    () -> state.cooldownPlayers.remove(uuid),
                    (long) state.getDelayTicks()
            );
        }
    }

    private Material getRandomMaterialFromMap(Map<Material, Double> map) {
        double rand = Math.random();
        double cumulative = 0.0;
        for (Map.Entry<Material, Double> entry : map.entrySet()) {
            cumulative += entry.getValue();
            if (rand <= cumulative) {
                return entry.getKey();
            }
        }
        return Material.DIRT; // fallback
    }
}
