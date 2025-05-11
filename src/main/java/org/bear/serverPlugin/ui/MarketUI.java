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
import java.util.ArrayList;

public class MarketUI {

    private static final String UI_TITLE = "Market";
    private static final int INVENTORY_SIZE = 36;

    public void openMarketUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, INVENTORY_SIZE, Component.text(UI_TITLE));

        inv.setItem(0, createMarketItem(Material.DIRT, 100, player, true));

        player.openInventory(inv);
    }

    private ItemStack createMarketItem(Material material, int cost, Player player, Boolean isStackable) {

        if (isStackable){
            openQuantityUI(player, material, cost);
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            boolean canAfford = checkPlayerCanAfford(player, cost);

            meta.setDisplayName(ChatColor.YELLOW + toTitleCase(material.name()));

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "A useful item");
            lore.add("");
            lore.add((canAfford ? ChatColor.GREEN : ChatColor.RED) + "Cost: " + cost + " crypto");

            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }

    private boolean checkPlayerCanAfford(Player player, int cost) {
        // Replace with actual balance check using PlayerData
        return true;
    }

    private String toTitleCase(String input) {
        String[] words = input.toLowerCase().split("_");
        StringBuilder titleCase = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                titleCase.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return titleCase.toString().trim();
    }
    private void openQuantityUI(Player player, Material material, int costPerItem) {
        Inventory quantityInv = Bukkit.createInventory(null, 36, Component.text("Select Quantity"));

        ItemStack add1 = createPane(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "+1");
        ItemStack add16 = createPane(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "+16");
        ItemStack add64 = createPane(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "+64");

        ItemStack sub1 = createPane(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "-1");
        ItemStack sub16 = createPane(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "-16");
        ItemStack sub64 = createPane(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "-64");

        ItemStack confirm = createPane(Material.EMERALD_BLOCK, ChatColor.GOLD + "Confirm Purchase");
        ItemStack cancel = createPane(Material.BARRIER, ChatColor.GRAY + "Cancel");

        quantityInv.setItem(10, add1);
        quantityInv.setItem(11, add16);
        quantityInv.setItem(12, add64);

        quantityInv.setItem(14, sub1);
        quantityInv.setItem(15, sub16);
        quantityInv.setItem(16, sub64);

        quantityInv.setItem(22, confirm);
        quantityInv.setItem(31, cancel);

        // Store custom metadata (e.g., using a map or metadata APIs) to track:
        // - selected material
        // - cost
        // - current quantity (initially 1)

        player.openInventory(quantityInv);
    }

    private ItemStack createPane(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

}
