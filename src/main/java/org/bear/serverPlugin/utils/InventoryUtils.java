package org.bear.serverPlugin.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryUtils {

    // Method to deserialize the inventory from a string format
    public static List<ItemStack> deserializeInventory(String inventoryString) {
        List<ItemStack> inventoryItems = new ArrayList<>();
        if (inventoryString != null && !inventoryString.isEmpty()) {
            String[] items = inventoryString.split(",");
            for (String itemData : items) {
                String[] itemParts = itemData.split(":");
                if (itemParts.length == 2) {
                    Material material = Material.getMaterial(itemParts[0]);
                    if (material != null) {
                        int amount = Integer.parseInt(itemParts[1]);
                        inventoryItems.add(new ItemStack(material, amount));
                    }
                }
            }
        }
        return inventoryItems;
    }

    // Method to serialize the inventory into a string format
    public static String serializeInventory(List<ItemStack> inventoryItems) {
        StringBuilder sb = new StringBuilder();
        for (ItemStack item : inventoryItems) {
            if (item != null) {
                sb.append(item.getType().toString()).append(":").append(item.getAmount()).append(",");
            }
        }
        return sb.toString();
    }
}
