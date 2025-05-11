package org.bear.serverPlugin.events.Inventory;

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

        if (title.equals("Phone") || title.equals("Upgrades") || title.contains("Generator") ) {
            event.setCancelled(true);  // ✅ Cancel the event ONLY if it's the "Phone" menu

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            //PHONE

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

            if (clickedItem.getType() == Material.DIAMOND_BLOCK &&
                    clickedItem.hasItemMeta() &&
                    clickedItem.getItemMeta().hasDisplayName() &&
                    clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Market")) {
                state.marketUI.openMarketUI(player);
            }

            //UPGRADES

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
                        state.genUI.openGenUI(player);
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
                        state.genUI.openGenUI(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "Not enough crypto!");
                    }
                }
            }

            if (clickedItem.getType() == Material.IRON_BLOCK &&
                    clickedItem.hasItemMeta() &&
                    clickedItem.getItemMeta().hasDisplayName() &&
                    clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Generator Upgrades")) {
                state.genMenuUI.openGenMenuUI(player);
            }

            if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                String displayName = clickedItem.getItemMeta().getDisplayName();

                for (int i = 1; i <= state.maxSlotLevel; i++) {
                    if (displayName.equals(ChatColor.GRAY + "Generator " + i)) {
                        state.genUI.openGenUI(player);
                        break;
                    }
                }
            }
        }
    }
}
