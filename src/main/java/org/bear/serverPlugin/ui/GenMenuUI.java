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

public class GenMenuUI {
    private final PluginState state;

    public GenMenuUI(PluginState state) {
        this.state = state;
    }


    public void openGenMenuUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("Generators"));
        for (int i = 1; i < state.getPlayerData(player.getUniqueId()).slotLevel+1; i++)
            inv.setItem(i-1, createGenItem(player, i));

        player.openInventory(inv);
    }

    public ItemStack createGenItem(Player player, int index) {
        ItemStack item = new ItemStack(Material.IRON_BLOCK); //iron for now
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "Generator " + index);

            meta.setLore(Arrays.asList(
                    ChatColor.DARK_GRAY + "Click for specs"
            ));

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }

}