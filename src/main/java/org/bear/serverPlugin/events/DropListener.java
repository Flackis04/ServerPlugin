package org.bear.serverPlugin.events;

import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.ui.PhoneUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DropListener implements Listener {

    private final PluginState state;
    private final PhoneUI phoneUI;

    public DropListener(PluginState state, PhoneUI phoneUI) {
        this.state = state;
        this.phoneUI = phoneUI;
    }

    @EventHandler
    public void onDropKey(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        ItemMeta itemMeta = droppedItem.getItemMeta();

        if (itemMeta != null && itemMeta.hasCustomModelData()) {
            event.setCancelled(true);
            player.sendMessage("§aYou opened a UI!");
            phoneUI.openPhoneUI(player);
        }
    }
}
