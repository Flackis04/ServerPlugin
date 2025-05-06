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
import org.bear.serverPlugin.ui.ScoreboardManager;
import org.bear.serverPlugin.ui.PhoneUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class InventoryListener implements Listener {

    private final PluginState state;

    public InventoryListener(PluginState state) {
        this.state = state;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());

        if (title.equals("Phone")) {
            event.setCancelled(true);  // ✅ Cancel the event ONLY if it's the "Phone" menu

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            if (clickedItem.getType() == Material.EMERALD_BLOCK) {
                int price = 500;

                if (state.delayLevel >= 16) {
                    player.sendMessage(ChatColor.RED + "There exists no better CPU at the moment. Invent your own maybe!");
                } else if (state.crypto >= price) {
                    state.crypto -= price;
                    state.delayLevel += 1;

                    player.sendMessage("New CPU chip installed: Intel Core i" + state.delayLevel);
                    state.scoreboardManager.updateCrypto(player, state.crypto);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    state.phoneUI.openPhoneUI(player);
                } else {
                    player.sendMessage(ChatColor.RED + "Not enough crypto!");
                }
            }
        }
    }
}
