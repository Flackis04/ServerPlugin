package org.bear.serverPlugin.ui;

import org.bear.serverPlugin.data.PlayerGenerator;
import org.bear.serverPlugin.utils.InventoryCoordinate;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GeneratorManagerUI extends UIBlueprint {
    private final Set<PlayerGenerator> generators;
    private final Map<Integer, PlayerGenerator> generatorsAtSlots = new HashMap<>();

    public GeneratorManagerUI(Set<PlayerGenerator> generators) {
        super(6, "Generators", false);
        this.generators = generators;
    }

    protected void updateInventory() {
        int i = 0;
        for (PlayerGenerator generator : generators) {
            generatorsAtSlots.put(i, generator);
            getInventory().setItem(i, createGenItem(generator));
            i++;
        }
    }

    protected boolean onSlotClick(Player player, InventoryCoordinate coordinate, ClickType clickType) {
        return false;
    }

    public ItemStack createGenItem(PlayerGenerator generator) {
        ItemStack item = new ItemStack(generator.block.getType());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + generator.name);

            meta.setLore(Arrays.asList(
                    ChatColor.DARK_GRAY + "Click for specs"
            ));

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }
}