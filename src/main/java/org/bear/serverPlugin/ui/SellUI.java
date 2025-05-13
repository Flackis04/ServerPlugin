package org.bear.serverPlugin.ui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SellUI implements Listener {
    private final Map<UUID, Inventory> openSellUIs = new HashMap<>();

    public void openSellUI(Player player) {
        Inventory inv = Bukkit.createInventory(player, 36, Component.text("SELL"));
        openSellUIs.put(player.getUniqueId(), inv);

        // Example: fill some slots with placeholder items
        // inv.setItem(13, yourCustomItem);

        player.openInventory(inv);
    }
}
