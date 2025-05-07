package org.bear.serverPlugin.ui;

import net.kyori.adventure.text.Component;
import org.bear.serverPlugin.data.PluginState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class SellUI {
    private final PluginState pluginState;
    public final Map<Player, Inventory> openSellUIs = new HashMap<>();

    public SellUI(PluginState pluginState) {
        this.pluginState = pluginState;
    }

    public void openSellUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, Component.text("Sell"));
        openSellUIs.put(player, inv); // Track it
        player.openInventory(inv);
    }

    public Inventory getSellUI(Player player) {
        return openSellUIs.get(player);
    }

    public void removeSellUI(Player player) {
        openSellUIs.remove(player);
    }
}