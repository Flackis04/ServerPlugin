package org.bear.serverPlugin.events;

import org.bear.serverPlugin.data.PluginState;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

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
                state.genIsActive = !state.genIsActive;
                state.phoneUI.openPhoneUI(player);
                player.sendMessage("gens activated: " + state.genIsActive);
            }

            if (clickedItem.getType() == Material.EMERALD_BLOCK &&
                    clickedItem.hasItemMeta() &&
                    clickedItem.getItemMeta().hasDisplayName() &&
                    clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "CPU")) {
                if (state.delayLevel >= state.maxDelayLevel) {
                    player.sendMessage(ChatColor.RED + "There exists no better CPU at the moment");
                } else {
                    int price = state.delayLevelCosts.getOrDefault(state.delayLevel, 1);
                    if (state.crypto >= price) {
                        state.crypto -= price;
                        state.delayLevel += 1;

                        player.sendMessage("New CPU chip installed: Intel Core i" + state.delayLevel);
                        state.scoreboardManager.updateCrypto(player, state.crypto);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        state.upgradeUI.openUpgradeUI(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "Not enough crypto!");
                    }
                }
            }
        }
    }
}
