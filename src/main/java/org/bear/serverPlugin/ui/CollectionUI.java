package org.bear.serverPlugin.ui;

import org.bear.serverPlugin.data.PluginState;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class CollectionUI {

    private final PluginState state;

    public CollectionUI(PluginState state) {
        this.state = state;
    }

    // Updates the collection UI by adding collected materials to the inventory
    public void updateCollectionUI(Player player) {
        Inventory inv = player.getOpenInventory().getTopInventory(); // Use the currently open inventory

        // Clear the inventory (optional)
        inv.clear();

        // Add all collected materials to the inventory
        int slot = 0;
        for (Material mat : state.getPlayerData(player.getUniqueId()).matInCollection) {
            if (slot >= inv.getSize()) break;  // Prevent overflow if collection is too large
            inv.setItem(slot, createCollectionMat(mat));
            slot++;
        }

        player.updateInventory(); // Force the inventory to update on the player's screen
    }

    // Creates an item representing a collected material
    public ItemStack createCollectionMat(Material mat) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + mat.name()); // Dynamically set material name
            meta.setLore(Arrays.asList(
                    ChatColor.DARK_GRAY + "Achieved on: " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()), // Example date
                    (ChatColor.DARK_GRAY + "Click for more info") // Example date
            ));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }
}
