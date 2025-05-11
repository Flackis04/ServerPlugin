package org.bear.serverPlugin.ui;

import net.kyori.adventure.text.Component;
import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.data.PlayerData;
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
        inv.setItem(10, createIslandUpgradeBtn(player));  // Using Island Expansion with its own level
        inv.setItem(12, createGenUpgradeMenuBtn());  // Using Island Expansion with its own level
        player.openInventory(inv);
    }

    public ItemStack createIslandUpgradeBtn(Player player) {
        PlayerData data = state.getPlayerData(player.getUniqueId());
        int currentLevel = data.islandExpansionLevel;  // Now using islandLevel
        Map<Integer, Integer> costs = state.islandExpansionLevelCosts;  // islandLevelCosts map
        int cost = costs.getOrDefault(currentLevel, 0);
        boolean canAfford = data.crypto >= cost;

        ItemStack item = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "Island Expansion");

            String costText = currentLevel == state.maxIslandExpansionLevel  // Ensure max level is tracked
                    ? ChatColor.RED + "Max level reached"
                    : (canAfford ? ChatColor.GREEN : ChatColor.RED) + "Cost: " + cost;

            meta.setLore(Arrays.asList(
                    ChatColor.DARK_GRAY + "Add a chunk to your island per level",
                    ChatColor.GRAY + "Level: " + currentLevel,  // Show current island level
                    costText
            ));

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }

    public ItemStack createGenUpgradeMenuBtn() {

        ItemStack item = new ItemStack(Material.IRON_BLOCK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "Generator Upgrades");

            meta.setLore(Arrays.asList(
                    ChatColor.DARK_GRAY + "Upgrade your Gens"
            ));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }
}
