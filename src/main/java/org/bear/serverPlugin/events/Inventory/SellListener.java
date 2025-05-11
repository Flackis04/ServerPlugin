package org.bear.serverPlugin.events.Inventory;

import org.bear.serverPlugin.data.PluginState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class SellListener implements Listener {
    private final PluginState state;

    public SellListener(PluginState state) {
        this.state = state;
    }

    @EventHandler
    public void onCloseUI(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        String title = event.getView().title().toString();

        if (title.equalsIgnoreCase("Sell")) {
            Inventory closedInventory = event.getInventory();
            Map<Material, Integer> foundItems = getMaterialIntegerMap(event, state.sellPrices);
            int totalProfit = 0;
            Set<Integer> processedSlots = new HashSet<>();

            // Handle sellable items
            if (!foundItems.isEmpty()) {
                for (int i = 0; i < closedInventory.getSize(); i++) {
                    ItemStack item = closedInventory.getItem(i);
                    if (item != null && item.getType() != Material.AIR) {
                        Material material = item.getType();
                        // Check if the item is sellable
                        if (state.valuables.containsKey(material)) {
                            ItemMeta meta = item.getItemMeta();
                            if (meta != null && meta.hasLore()) {
                                for (String line : meta.getLore()) {
                                    if (line.contains("sell price:")) {
                                        int amount = item.getAmount();
                                        int pricePerItem = state.sellPrices.getOrDefault(material, 0);
                                        int value = amount * pricePerItem;
                                        totalProfit += value;

                                        player.sendMessage("§6" + material + ": §f" + amount + " x " + pricePerItem + "C = §a" + value + "C");
                                        processedSlots.add(i);
                                    }
                                }
                            }
                        }
                    }
                }

                player.sendMessage("§aTotal Profit: §2" + totalProfit + "C");
                state.getPlayerData(player.getUniqueId()).crypto += totalProfit;
            }

            // Return non-sellable items to the player
            for (int i = 0; i < closedInventory.getSize(); i++) {
                if (processedSlots.contains(i)) continue;
                ItemStack item = closedInventory.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    player.getInventory().addItem(item);
                }
            }
        }
    }

    private Map<Material, Integer> getMaterialIntegerMap(InventoryCloseEvent event, Map<Material, Integer> sellPrices) {
        Inventory closedInventory = event.getInventory();
        Map<Material, Integer> foundItems = new HashMap<>();

        for (ItemStack item : closedInventory.getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;
            Material material = item.getType();

            if (state.valuables.containsKey(material)) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasLore()) {
                    for (String line : meta.getLore()) {
                        if (line.contains("sell price:")) {
                            int amount = item.getAmount();
                            foundItems.put(material, foundItems.getOrDefault(material, 0) + amount);
                            break;
                        }
                    }
                }
            }
        }

        return foundItems;
    }
}
