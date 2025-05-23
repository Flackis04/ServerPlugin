package org.bear.serverPlugin.ui;

import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.utils.InventoryCoordinate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

public class CollectionUI extends UIBlueprint {
    private final Set<Material> seenMaterials;

    public CollectionUI(Set<Material> seenMaterials) {
        super(6, "Collection", false);
        this.seenMaterials = seenMaterials;
    }

    protected void updateInventory() {
        int slot = 0;
        for (Material mat : seenMaterials) {
            // TODO: Pagination
            if (slot >= getInventory().getSize()) break;  // Prevent overflow if collection is too large
            getInventory().setItem(slot, itemFromMaterial(mat));
            slot++;
        }
    }

    protected boolean onSlotClick(Player player, InventoryCoordinate coordinate, ClickType clickType) {
        return false;
    }

    // Creates an item representing a collected material
    public ItemStack itemFromMaterial(Material mat) {
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
