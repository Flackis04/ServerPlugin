package org.bear.serverPlugin.events;

import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.ui.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class InventoryListener implements Listener {

    private final PluginState state;
    private final UpgradeUI upgradeUI;
    private final SellUI sellUI;
    private final CollectionUI collectionUI;

    public InventoryListener(PluginState state, UpgradeUI upgradeUI, SellUI sellUI, CollectionUI collectionUI) {
        this.state = state;
        this.upgradeUI = upgradeUI;
        this.sellUI = sellUI;
        this.collectionUI = collectionUI;
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());

        if (title.equals("Phone") || title.equals("Upgrades")) {
            event.setCancelled(true);  // ✅ Cancel the event ONLY if it's the "Phone" menu

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            // clickedItem.getItemMeta().getCustomModelDataComponent().getStrings().get(0).equals("phone")
            if (clickedItem.getType() == Material.DIRT) {
                state.upgradeUI.openUpgradeUI(player);
            }
            if (clickedItem.getType() == Material.GOLD_BLOCK) {
                state.sellUI.openSellUI(player);
            }
            if (clickedItem.getType() == Material.BOOK) {
                state.collectionUI.updateCollectionUI(player);
            }

            if (clickedItem.getType() == Material.EMERALD_BLOCK) {
                if (state.delayLevel >= 16) {
                    player.sendMessage(ChatColor.RED + "There exists no better CPU at the moment");
                } else {
                    int nextLevel = state.delayLevel + 1;
                    int price = state.delayLevelCosts.getOrDefault(nextLevel, 500);

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
