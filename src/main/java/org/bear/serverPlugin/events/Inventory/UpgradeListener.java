package org.bear.serverPlugin.events.Inventory;

import org.bear.serverPlugin.data.PluginState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.ChatColor;

public class UpgradeListener implements Listener {
    private final PluginState state;

    public UpgradeListener(PluginState state) {
        this.state = state;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().title().toString();

        if (title.equals("Upgrades")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            Material clickedItem = event.getCurrentItem().getType();

            // CPU upgrade
            if (clickedItem == Material.EMERALD_BLOCK && event.getCurrentItem().hasItemMeta() &&
                    event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GRAY + "CPU")) {
                // Handle CPU upgrade logic...
            }

            // Storage upgrade
            if (clickedItem == Material.BARREL && event.getCurrentItem().hasItemMeta() &&
                    event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Storage")) {
                // Handle storage upgrade logic...
            }
        }
    }
}
