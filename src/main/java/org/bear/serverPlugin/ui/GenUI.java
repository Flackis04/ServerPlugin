package org.bear.serverPlugin.ui;

import net.kyori.adventure.text.Component;
import org.bear.serverPlugin.data.PlayerData;
import org.bear.serverPlugin.data.PluginState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Map;

public class GenUI {
    private final PluginState state;

    public GenUI(PluginState state) {
        this.state = state;
    }


    public void openGenUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("Generator"));
        inv.setItem(22, createCpuItem(player));
        inv.setItem(24, createSlotItem(player));

        player.openInventory(inv);
    }

    public ItemStack createCpuItem(Player player) {
        PlayerData data = state.getPlayerData(player.getUniqueId());
        int currentLevel = data.delayLevel;
        Map<Integer, Integer> costs = state.delayLevelCosts;
        int cost = costs.getOrDefault(currentLevel, 0);
        boolean canAfford = data.crypto >= cost;

        ItemStack item = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "CPU");

            String costText = currentLevel == state.maxDelayLevel
                    ? ChatColor.RED + "Max level reached"
                    : (canAfford ? ChatColor.GREEN : ChatColor.RED) + "Cost: " + cost;

            meta.setLore(Arrays.asList(
                    ChatColor.DARK_GRAY + "Makes your gens faster",
                    ChatColor.GRAY + "Level: " + currentLevel + " (" + (state.getDelayTicks(player) / 20f) + "s)",
                    costText
            ));

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }

    public ItemStack createSlotItem(Player player) {
        PlayerData data = state.getPlayerData(player.getUniqueId());
        int currentLevel = data.slotLevel;
        Map<Integer, Integer> costs = state.slotLevelCosts;
        int cost = costs.getOrDefault(currentLevel, 0);
        boolean canAfford = data.crypto >= cost;

        ItemStack item = new ItemStack(Material.BARREL);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "Storage");

            String costText = currentLevel == state.maxSlotLevel
                    ? ChatColor.RED + "Max level reached"
                    : (canAfford ? ChatColor.GREEN : ChatColor.RED) + "Cost: " + cost;

            meta.setLore(Arrays.asList(
                    ChatColor.DARK_GRAY + "Place an additional Gen per level",
                    ChatColor.GRAY + "Level: " + currentLevel,
                    costText
            ));

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }
}