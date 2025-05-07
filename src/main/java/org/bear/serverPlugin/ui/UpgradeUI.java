package org.bear.serverPlugin.ui;

import net.kyori.adventure.text.Component;
import org.bear.serverPlugin.data.PluginState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Map;

public class UpgradeUI {

    private final PluginState state;

    public UpgradeUI(PluginState state) {
        this.state = state;
    }

    public void openUpgradeUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("Upgrades"));
        inv.setItem(22, createCpuItem());
        player.openInventory(inv);
    }

    public ItemStack createCpuItem() {
        ItemStack item = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta meta = item.getItemMeta();

        Map<Integer, Integer> delayLevelCosts = state.delayLevelCosts;

// Get the cost for the current delayLevel
        int currentCost = delayLevelCosts.getOrDefault(state.delayLevel, 0);

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "CPU");
            boolean canAfford = state.crypto >= currentCost;
            meta.setLore(Arrays.asList(
                    ChatColor.DARK_GRAY + "Regulates your phone's clock speed",
                    ChatColor.GRAY + "Level: " + state.delayLevel + " (" + (state.getDelayTicks() / 20f) + "s)",
                    (canAfford ? ChatColor.GREEN : ChatColor.RED) + "Cost: " + currentCost
            ));

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }


        return item;
    }
}
