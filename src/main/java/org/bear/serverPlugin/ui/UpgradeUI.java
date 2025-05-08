package org.bear.serverPlugin.ui;

import net.kyori.adventure.text.Component;
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

public class UpgradeUI {

    private final PluginState state;

    public UpgradeUI(PluginState state) {
        this.state = state;
    }

    public void openUpgradeUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("Upgrades"));
        inv.setItem(22, createCpuItem());
        inv.setItem(39, createSlotItem());
        inv.setItem(41, createIslandItem());
        player.openInventory(inv);
    }

    public ItemStack createCpuItem() {
        ItemStack item = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta meta = item.getItemMeta();

        Map<Integer, Integer> delayLevelCosts = state.delayLevelCosts;

// Get the cost for the current delayLevel
        int currentCost = delayLevelCosts.getOrDefault(state.delayLevel, 0);

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "CPU");

            boolean canAfford = state.crypto >= currentCost;
            String costText;

            // Check if max level is reached
            if (state.delayLevel == state.maxDelayLevel) {
                costText = ChatColor.RED + "Max level reached"; // Display max level reached if on max level
            } else {
                costText = (canAfford ? ChatColor.GREEN : ChatColor.RED) + "Cost: " + currentCost; // Otherwise, show the cost
            }

            // Set lore with the appropriate message
            meta.setLore(Arrays.asList(
                    ChatColor.DARK_GRAY + "Makes your gens faster",
                    ChatColor.GRAY + "Level: " + state.delayLevel + " (" + (state.getDelayTicks() / 20f) + "s)",
                    costText // Add the cost or "Max level reached"
            ));

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack createSlotItem() {
        ItemStack item = new ItemStack(Material.BARREL);
        ItemMeta meta = item.getItemMeta();

        Map<Integer, Integer> slotLevelCosts = state.slotLevelCosts;

// Get the cost for the current delayLevel
        int currentCost = slotLevelCosts.getOrDefault(state.slotLevel, 0);

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "Slots");

            boolean canAfford = state.crypto >= currentCost;
            String costText;

            // Check if max level is reached
            if (state.slotLevel == state.maxSlotLevel) {
                costText = ChatColor.RED + "Max level reached"; // Display max level reached if on max level
            } else {
                costText = (canAfford ? ChatColor.GREEN : ChatColor.RED) + "Cost: " + currentCost; // Otherwise, show the cost
            }

            // Set lore with the appropriate message
            meta.setLore(Arrays.asList(
                    ChatColor.DARK_GRAY + "Enables you to place an additional Gen per level",
                    ChatColor.GRAY + "Level: " + state.slotLevel,
                    costText // Add the cost or "Max level reached"
            ));

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }
    public ItemStack createIslandItem() {
        ItemStack item = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta meta = item.getItemMeta();

        Map<Integer, Integer> delayLevelCosts = state.delayLevelCosts;

// Get the cost for the current delayLevel
        int currentCost = delayLevelCosts.getOrDefault(state.delayLevel, 0);

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "Island Expansion");

            boolean canAfford = state.crypto >= currentCost;
            String costText;

            // Check if max level is reached
            if (state.delayLevel == state.maxDelayLevel) {
                costText = ChatColor.RED + "Max level reached"; // Display max level reached if on max level
            } else {
                costText = (canAfford ? ChatColor.GREEN : ChatColor.RED) + "Cost: " + currentCost; // Otherwise, show the cost
            }

            // Set lore with the appropriate message
            meta.setLore(Arrays.asList(
                    ChatColor.DARK_GRAY + "Enables you to add a chunk to your island per level",
                    ChatColor.GRAY + "Level: " + state.delayLevel + " (" + (state.getDelayTicks() / 20f) + "s)",
                    costText // Add the cost or "Max level reached"
            ));

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }
}
