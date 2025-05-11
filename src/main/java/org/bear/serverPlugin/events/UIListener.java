package org.bear.serverPlugin.events;

import org.bear.serverPlugin.data.PluginState;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class UIListener implements Listener {

    private final PluginState state;

    public UIListener(PluginState state) {
        this.state = state;
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());

        if (title.equals("Phone") || title.equals("Upgrades")) {
            event.setCancelled(true);  // ✅ Cancel the event ONLY if it's the "Phone" menu

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            if (clickedItem.getType() == Material.DIRT &&
                    clickedItem.hasItemMeta() &&
                    clickedItem.getItemMeta().hasDisplayName() &&
                    clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Upgrades")) {
                state.upgradeUI.openUpgradeUI(player);
            }

            if (clickedItem.getType() == Material.GOLD_BLOCK &&
                    clickedItem.hasItemMeta() &&
                    clickedItem.getItemMeta().hasDisplayName() &&
                    clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "SELL")) {
                state.sellUI.openSellUI(player);
            }
            if (clickedItem.getType() == Material.BOOK &&
                    clickedItem.hasItemMeta() &&
                    clickedItem.getItemMeta().hasDisplayName() &&
                    clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Collection")) {
                state.collectionUI.updateCollectionUI(player);
            }

            if (clickedItem.getType() == Material.REDSTONE &&
                    clickedItem.hasItemMeta() &&
                    clickedItem.getItemMeta().hasDisplayName() &&
                    clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Activation")) {
                state.getPlayerData(player.getUniqueId()).genIsActive = !state.getPlayerData(player.getUniqueId()).genIsActive;
                state.phoneUI.openPhoneUI(player);
                player.sendMessage("gens activated: " + state.getPlayerData(player.getUniqueId()).genIsActive);
            }

            if (clickedItem.getType() == Material.EMERALD_BLOCK &&
                    clickedItem.hasItemMeta() &&
                    clickedItem.getItemMeta().hasDisplayName() &&
                    clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "CPU")) {
                if (state.getPlayerData(player.getUniqueId()).delayLevel >= state.maxDelayLevel) {
                    player.sendMessage(ChatColor.RED + "There exists no better CPU at the moment");
                } else {
                    int price = state.delayLevelCosts.getOrDefault(state.getPlayerData(player.getUniqueId()).delayLevel, 1);
                    if (state.getPlayerData(player.getUniqueId()).crypto >= price) {
                        state.getPlayerData(player.getUniqueId()).crypto -= price;
                        state.getPlayerData(player.getUniqueId()).delayLevel += 1;


                        player.sendMessage("New CPU chip installed: Intel Core i" + state.getPlayerData(player.getUniqueId()).delayLevel);
                        state.scoreboardManager.updateCrypto(player, state.getPlayerData(player.getUniqueId()).crypto);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        state.upgradeUI.openUpgradeUI(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "Not enough crypto!");
                    }
                }
            }

            if (clickedItem.getType() == Material.BARREL &&
                    clickedItem.hasItemMeta() &&
                    clickedItem.getItemMeta().hasDisplayName() &&
                    clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Storage")) {
                if (state.getPlayerData(player.getUniqueId()).slotLevel >= state.maxSlotLevel) {
                    player.sendMessage(ChatColor.RED + "Your Storage is maxed out brother");
                } else {
                    int price = state.slotLevelCosts.getOrDefault(state.getPlayerData(player.getUniqueId()).slotLevel, 1);
                    if (state.getPlayerData(player.getUniqueId()).crypto >= price) {
                        state.getPlayerData(player.getUniqueId()).crypto -= price;
                        state.getPlayerData(player.getUniqueId()).slotLevel += 1;


                        player.sendMessage("Storage count increased to " + state.getPlayerData(player.getUniqueId()).slotLevel);
                        state.scoreboardManager.updateCrypto(player, state.getPlayerData(player.getUniqueId()).crypto);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        state.upgradeUI.openUpgradeUI(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "Not enough crypto!");
                    }
                }
            }

            if (clickedItem.getType() == Material.IRON_BLOCK &&
                    clickedItem.hasItemMeta() &&
                    clickedItem.getItemMeta().hasDisplayName() &&
                    clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Generator Upgrades")) {
                state.genUI.openGenUI(player);
            }
        }
    }

    @EventHandler
    public void onCloseUI(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        // Get the title as plain text
        String title = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                .serialize(event.getView().title());

        if (title.equalsIgnoreCase("Sell")) {
            Inventory closedInventory = event.getInventory();
            Map<Material, Integer> foundItems = getMaterialIntegerMap(event, state.sellPrices);

            int totalProfit = 0;
            Set<Integer> processedSlots = new HashSet<>();

            // Step 1: Handle sellable items
            if (!foundItems.isEmpty()) {
                for (int i = 0; i < closedInventory.getSize(); i++) {
                    ItemStack item = closedInventory.getItem(i);
                    if (item == null || item.getType() == Material.AIR) continue;

                    Material material = item.getType();
                    if (!state.valuables.containsKey(material)) continue;

                    ItemMeta meta = item.getItemMeta();
                    if (meta != null && meta.hasLore()) {
                        for (String line : meta.getLore()) {
                            if (line != null && line.toLowerCase().contains("sell price:")) {
                                int amount = item.getAmount();
                                int pricePerItem = state.sellPrices.getOrDefault(material, 0);
                                int value = amount * pricePerItem;
                                totalProfit += value;

                                player.sendMessage("§6" + material + ": §f" + amount + " x " + pricePerItem + "C = §a" + value + "C");

                                // Mark slot as handled
                                processedSlots.add(i);
                                break;
                            }
                        }
                    }
                }

                player.sendMessage("§aTotal Profit: §2" + totalProfit + "C");
                state.getPlayerData(player.getUniqueId()).crypto += totalProfit;
                state.scoreboardManager.updateCrypto(player, state.getPlayerData(player.getUniqueId()).crypto);
            } else {
                player.sendMessage("§cNo sellable valuables found.");
            }

            // Step 2: Return non-sellable items to player
            for (int i = 0; i < closedInventory.getSize(); i++) {
                if (processedSlots.contains(i)) continue; // already sold
                ItemStack item = closedInventory.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    player.getInventory().addItem(item); // safely adds remaining items back
                }
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
