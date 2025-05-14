package org.bear.serverPlugin.ui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.utils.ItemUtils;

import java.util.List;
import java.util.ArrayList;

public class MarketUI {

    private static final String UI_TITLE = "Market";
    private static final int INVENTORY_SIZE = 27;
    private final PluginState state;
    public MarketUI(PluginState state) {
        this.state = state;
    }
    public void openMarketUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, INVENTORY_SIZE, Component.text(UI_TITLE));

        inv.addItem(createMarketItem(Material.DIRT, 2, player, true));
        inv.addItem(createMarketItem(Material.WOODEN_AXE, 100, player, false));
        inv.addItem(createMarketItem(Material.SPLASH_POTION, 10000, player, false));
        inv.addItem(createMarketItemFromStack(ItemUtils.getPhone(), 25000, player, false));

        player.openInventory(inv);
    }

    private ItemStack createMarketItem(Material material, int cost, Player player, Boolean isStackable) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            boolean canAfford = checkPlayerCanAfford(player, cost);

            meta.displayName(Component.text(toTitleCase(material.name())).color(NamedTextColor.YELLOW));

            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("A useful item").color(NamedTextColor.GRAY));
            lore.add(Component.empty());
            lore.add(Component.text("Cost: " + cost + " crypto").color(canAfford ? NamedTextColor.GREEN : NamedTextColor.RED));

            meta.lore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createMarketItemFromStack(ItemStack item, int cost, Player player, Boolean isStackable) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            boolean canAfford = checkPlayerCanAfford(player, cost);

            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("A useful item").color(NamedTextColor.GRAY));
            lore.add(Component.empty());
            lore.add(Component.text("Cost: " + cost + " crypto").color(canAfford ? NamedTextColor.GREEN : NamedTextColor.RED));

            meta.lore(lore);
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
    public void openQuantityUI(Player player, Material material, int costPerItem, int quantity) {
        Inventory quantityInv = createQuantityInventory(material);

        player.openInventory(quantityInv);
    }

    public void openNoQuantityUI(Player player, Material material, int cost) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("No Quantity"));

        inv.setItem(27, new ItemStack(material));

        ItemStack add1 = createPane(Material.LIME_STAINED_GLASS_PANE, Component.text("+1").color(NamedTextColor.GREEN));
        ItemStack add10 = createPane(Material.LIME_STAINED_GLASS_PANE, Component.text("+10").color(NamedTextColor.GREEN));
        ItemStack add64 = createPane(Material.LIME_STAINED_GLASS_PANE, Component.text("+64").color(NamedTextColor.GREEN));

        ItemStack sub1 = createPane(Material.RED_STAINED_GLASS_PANE, Component.text("-1").color(NamedTextColor.RED));
        ItemStack sub10 = createPane(Material.RED_STAINED_GLASS_PANE, Component.text("-10").color(NamedTextColor.RED));
        ItemStack sub64 = createPane(Material.RED_STAINED_GLASS_PANE, Component.text("-64").color(NamedTextColor.RED));

        ItemStack confirm = createPane(Material.EMERALD_BLOCK, Component.text("Confirm Purchase").color(NamedTextColor.GOLD));
        ItemStack cancel = createPane(Material.BARRIER, Component.text("Cancel").color(NamedTextColor.GRAY));

        inv.setItem(11, confirm);
        inv.setItem(15, cancel);

        inv.setItem(13, new ItemStack(material));

        player.openInventory(inv);
    }

    public Inventory createQuantityInventory(Material material) {
        Inventory inventory = Bukkit.createInventory(null, 27, Component.text("Select Quantity"));

        inventory.setItem(15, createPane(Material.LIME_STAINED_GLASS_PANE, Component.text("+1").color(NamedTextColor.GREEN)));
        inventory.setItem(16, createPane(Material.LIME_STAINED_GLASS_PANE, Component.text("+10").color(NamedTextColor.GREEN)));
        inventory.setItem(17, createPane(Material.LIME_STAINED_GLASS_PANE, Component.text("+64").color(NamedTextColor.GREEN)));

        inventory.setItem(9, createPane(Material.RED_STAINED_GLASS_PANE, Component.text("-64").color(NamedTextColor.RED)));
        inventory.setItem(10, createPane(Material.RED_STAINED_GLASS_PANE, Component.text("-10").color(NamedTextColor.RED)));
        inventory.setItem(11, createPane(Material.RED_STAINED_GLASS_PANE, Component.text("-1").color(NamedTextColor.RED)));

        inventory.setItem(13, new ItemStack(material));
        inventory.setItem(21, createPane(Material.BARRIER, Component.text("Cancel").color(NamedTextColor.GRAY)));
        inventory.setItem(23, createPane(Material.EMERALD_BLOCK, Component.text("Confirm").color(NamedTextColor.GREEN)));

        return inventory;
    }

    private ItemStack createPane(Material material, Component name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(name);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }
}