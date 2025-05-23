package org.bear.serverPlugin.ui;

import net.kyori.adventure.text.Component;
import org.bear.serverPlugin.utils.InventoryCoordinate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SellUI extends UIBlueprint {
    public SellUI() {
        super(4, "Sell", false);
    }

    protected void updateInventory(){

    }

    protected boolean onSlotClick(Player player, InventoryCoordinate coordinate, ClickType clickType){
        return false;
    }
}
