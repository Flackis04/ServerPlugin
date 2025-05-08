package org.bear.serverPlugin.events;

import org.bear.serverPlugin.data.PluginState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class InteractListener implements Listener {

    private final PluginState state;

    public InteractListener(PluginState state) {
        this.state = state;
    }
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != RIGHT_CLICK_AIR && event.getAction() != RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = itemInHand.getItemMeta();

        if (itemMeta != null && itemMeta.hasCustomModelData() && itemMeta.getCustomModelDataComponent().getStrings().getFirst().equals("phone")) {
            event.setCancelled(true);
            state.phoneUI.openPhoneUI(player);
        }
    }

    @EventHandler
    public void onCloseUI(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        // Get the title as plain text
        String title = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                .serialize(event.getView().title());

        // Check if it's the Sell UI
        if (title.equalsIgnoreCase("Sell")) {

            // Call getMaterialIntegerMap with the sellPrices
            Map<Material, Integer> foundItems = getMaterialIntegerMap(event, state.sellPrices);

            if (!foundItems.isEmpty()) {
                int totalProfit = 0;

                // Calculate and print the total profit
                for (Map.Entry<Material, Integer> entry : foundItems.entrySet()) {
                    Material material = entry.getKey();
                    int amount = entry.getValue();
                    int pricePerItem = state.sellPrices.getOrDefault(material, 0);
                    int value = amount * pricePerItem;
                    totalProfit += value;

                    player.sendMessage("§6" + material + ": §f" + amount + " x " + pricePerItem + "C = §a" + value + "C");
                }

                player.sendMessage("§aTotal Profit: §2" + totalProfit + "C");
                state.crypto = state.crypto + totalProfit;
                state.scoreboardManager.updateCrypto(player, state.crypto);
            } else {
                player.sendMessage("§cNo sellable valuables found.");
            }
        }
    }


    private @NotNull Map<Material, Integer> getMaterialIntegerMap(@NotNull InventoryCloseEvent event, @NotNull Map<Material, Integer> sellPrices) {
        Inventory closedInventory = event.getInventory();
        Map<Material, Integer> foundItems = new HashMap<>();

        for (ItemStack item : closedInventory.getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;
            Material material = item.getType();

            if (!state.valuables.containsKey(material)) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasLore()) {
                for (String line : Objects.requireNonNull(meta.getLore())) {
                    if (line != null && line.toLowerCase().contains("sell price:")) {
                        int amount = item.getAmount();
                        foundItems.put(material, foundItems.getOrDefault(material, 0) + amount);
                        break; // No need to check more lore lines once the sell price is found
                    }
                }
            }
        }

        return foundItems;
    }
}
