package org.bear.serverPlugin.ui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class SellUI {
    public final Map<Player, Inventory> openSellUIs = new HashMap<>();

    public void openSellUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, Component.text("SELL"));
        openSellUIs.put(player, inv); // Track it
        player.openInventory(inv);
    }
}