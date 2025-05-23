package org.bear.serverPlugin.ui;

import org.bear.serverPlugin.data.PlayerData;
import org.bear.serverPlugin.data.PlayerGenerator;
import org.bear.serverPlugin.utils.InventoryCoordinate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class GeneratorUI extends UIBlueprint {
    private final PlayerData playerData;
    private final PlayerGenerator generator;

    public GeneratorUI(PlayerData playerData, PlayerGenerator generator) {
        super(6, "Generator", false);
        this.playerData = playerData;
        this.generator = generator;
    }

    protected void updateInventory() {
        getInventory().setItem(10, createCpuItem());
    }

    protected boolean onSlotClick(Player player, InventoryCoordinate coordinate, ClickType clickType) {
        return false;
    }

    public ItemStack createCpuItem() {
        //int cost = costs.getOrDefault(currentLevel, 0);
        int cost = 2;

        ItemStack item = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "CPU");

            String costText = generator.delayLevel == generator.maxDelayLevel
                    ? ChatColor.RED + "Max level reached"
                    : (playerData.crypto >= cost ? ChatColor.GREEN : ChatColor.RED) + "Cost: " + cost;

            meta.setLore(Arrays.asList(
                    ChatColor.DARK_GRAY + "Makes your Gens faster",
                    ChatColor.GRAY + "Level: " + generator.delayLevel + " (" + (generator.getDelayTicks() / 20) + "s)",
                    costText
            ));

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }
}