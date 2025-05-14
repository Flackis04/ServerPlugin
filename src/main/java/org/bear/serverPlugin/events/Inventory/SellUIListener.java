package org.bear.serverPlugin.events.Inventory;

import net.kyori.adventure.text.TextComponent;
import org.bear.serverPlugin.data.PluginState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SellUIListener implements Listener {
    private final PluginState state;

    public SellUIListener(PluginState state) {
        this.state = state;
    }

    @EventHandler
    public void onCloseUI(@NotNull InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        String title = ((TextComponent) event.getView().title()).content();

        if ( title.equalsIgnoreCase("SELL")) {
            Inventory closedInventory = event.getInventory();
            Map<Material, Integer> foundItems = getMaterialIntegerMap(player, event, state.getPlayerData(player.getUniqueId()).sellPrices);
            int totalProfit = 0;
            Set<Integer> processedSlots = new HashSet<>();

            // Handle sellable items
            if (!foundItems.isEmpty()) {
                for (int i = 0; i < closedInventory.getSize(); i++) {
                    ItemStack item = closedInventory.getItem(i);
                    if (item != null && item.getType() != Material.AIR) {
                        Material material = item.getType();
                        // Check if the item is sellable
                        if (state.getPlayerData(player.getUniqueId()).valuables.containsKey(material)) {
                            ItemMeta meta = item.getItemMeta();
                            if (meta != null && meta.hasLore()) {
                                for (String line : meta.getLore()) {
                                    if (line.contains("Sell price:")) {
                                        int amount = item.getAmount();
                                        int pricePerItem = state.getPlayerData(player.getUniqueId()).sellPrices.getOrDefault(material, 0);
                                        int value = amount * pricePerItem;
                                        totalProfit += value;
                                        processedSlots.add(i);
                                    }
                                }
                            }
                        }
                    }
                }
                player.sendMessage("§aSold valuables for: §2" + totalProfit + "C");
                state.getPlayerData(player.getUniqueId()).crypto += totalProfit;
                state.scoreboardManager.updateCrypto(player, state.getPlayerData(player.getUniqueId()).crypto);
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

    private Map<Material, Integer> getMaterialIntegerMap(Player player, InventoryCloseEvent event, Map<Material, Integer> sellPrices) {
        Inventory closedInventory = event.getInventory();
        Map<Material, Integer> foundItems = new HashMap<>();

        for (ItemStack item : closedInventory.getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;
            Material material = item.getType();

            if (state.getPlayerData(player.getUniqueId()).valuables.containsKey(material)) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasLore()) {
                    for (String line : meta.getLore()) {
                        if (line.contains("Sell price:")) {
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
