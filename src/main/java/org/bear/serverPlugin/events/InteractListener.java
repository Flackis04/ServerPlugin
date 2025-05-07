package org.bear.serverPlugin.events;

import org.bear.serverPlugin.data.PluginState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
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
        UUID uuid = player.getUniqueId();

        // Check cooldown
        if (state.cooldownPlayers.contains(uuid)) {
            player.sendMessage("§cWait for cooldown");
            return;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = itemInHand.getItemMeta();

        if (itemMeta != null && itemMeta.hasCustomModelData() && itemMeta.getCustomModelDataComponent().getStrings().get(0).equals("phone")) {
            // Give random material from weighted valuables map
            Material mat = getRandomMaterialFromMap(state.valuables);
            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                // Set a custom description based on the material
                List<String> lore = new ArrayList<>();
                String description = "Sell price: " + state.sellPrices.get(mat); // Define your description method
                lore.add(description);

                meta.setLore(lore);
                item.setItemMeta(meta);
            }

            player.getInventory().addItem(item);
            player.sendMessage("§aYou got " + mat.toString().toLowerCase());
            state.seenMaterials.put(mat, true);

            if (state.seenMaterials.getOrDefault(mat, false)) {
                // Only update if the material is newly added to the collection
                if (!state.matInCollection.contains(mat)) {
                    state.matInCollection.add(mat);
                    player.sendMessage("seen: " + state.seenMaterials);
                    player.sendMessage("mats: " + state.matInCollection);
                    state.collectionUI.createCollectionMat(mat); // Optional: if needed to build UI items
                    state.collectionUI.updateCollectionUI(player); // Refresh the UI
                }
            }

            // Add to cooldown and schedule removal
            state.cooldownPlayers.add(uuid);
            Bukkit.getScheduler().runTaskLater(
                    Bukkit.getPluginManager().getPlugin("ServerPlugin"),
                    () -> state.cooldownPlayers.remove(uuid),
                    (long) state.getDelayTicks()
            );
        }
    }

    private Material getRandomMaterialFromMap(Map<Material, Double> map) {
        double rand = Math.random();
        double cumulative = 0.0;
        for (Map.Entry<Material, Double> entry : map.entrySet()) {
            cumulative += entry.getValue();
            if (rand <= cumulative) {
                return entry.getKey();
            }
        }
        return Material.DIRT; // fallback
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

                player.sendMessage("§aTotal Profit: §2C" + totalProfit);
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
                for (String line : meta.getLore()) {
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
