package org.bear.serverPlugin.ui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class PhoneUI {

    public void openPhoneUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("Phone"));
        inv.setItem(11, createUpgradesBtn());
        inv.setItem(13, createSellBtn());
        inv.setItem(15, createCollectionBtn());
        player.openInventory(inv);
    }

    private ItemStack createSellBtn() {
        ItemStack item = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "SELL");
            meta.setLore(List.of(
                    ChatColor.DARK_GRAY + "Sell duplicates"
            ));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createUpgradesBtn() {
        ItemStack item = new ItemStack(Material.DIRT); //change to phone
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "Upgrades");
            meta.setLore(List.of(
                    ChatColor.DARK_GRAY + "Sell duplicates"
            ));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createCollectionBtn() {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "Collection");
            meta.setLore(List.of(
                    ChatColor.DARK_GRAY + "Blocks you've discovered"
            ));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }
}
