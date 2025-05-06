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

public class PhoneUI {

    private final PluginState state;

    public PhoneUI(PluginState state) {
        this.state = state;
    }

    public void openPhoneUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("Phone"));
        inv.setItem(22, createCpuItem());
        player.openInventory(inv);
    }

    private ItemStack createCpuItem() {
        ItemStack item = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "CPU");
            meta.setLore(Arrays.asList(
                    ChatColor.DARK_GRAY + "Regulates your phone's clock speed",
                    ChatColor.GRAY + "Current Level : " + state.delayLevel + " : " + (state.getDelayTicks() / 20f) + "s"
            ));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }
}
