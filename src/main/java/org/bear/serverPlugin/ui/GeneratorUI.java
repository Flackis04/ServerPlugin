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

public class GeneratorUI extends UIBlueprint {
    private final PlayerData playerData;
    private final PlayerGenerator generator;

    public GeneratorUI(PlayerData playerData, PlayerGenerator generator) {
        super(6, "Generator", false);
        this.playerData = playerData;
        this.generator = generator;
    }


    public void openGenUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("Generator"));
        inv.setItem(10, createCpuItem(player));
        inv.setItem(12, createSlotItem(player));

        player.openInventory(inv);
    }

    public ItemStack createCpuItem(Player player) {
        PlayerData data = state.getPlayerData(player.getUniqueId());
        //int currentLevel = data.delayLevel;
        int currentLevel = 2;
        //int cost = costs.getOrDefault(currentLevel, 0);
        int cost = 2;
        boolean canAfford = data.crypto >= cost;

        ItemStack item = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "CPU");

            String costText = currentLevel == state.maxDelayLevel
                    ? ChatColor.RED + "Max level reached"
                    : (canAfford ? ChatColor.GREEN : ChatColor.RED) + "Cost: " + cost;

            meta.setLore(Arrays.asList(
                    ChatColor.DARK_GRAY + "Makes your Gens faster",
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
        int currentLevel = data.maxGenerators;
        //int cost = costs.getOrDefault(currentLevel, 0);
        int cost = 2;
        boolean canAfford = data.crypto >= cost;

        ItemStack item = new ItemStack(Material.BARREL);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "Storage");

            String costText = currentLevel == state.maxSlotLevel
                    ? ChatColor.RED + "Max level reached"
                    : (canAfford ? ChatColor.GREEN : ChatColor.RED) + "Cost: " + cost;

            meta.setLore(Arrays.asList(
                    ChatColor.DARK_GRAY + "Enables you to place an additional Gen per level",
                    ChatColor.GRAY + "Level: " + currentLevel,
                    costText
            ));

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }
}