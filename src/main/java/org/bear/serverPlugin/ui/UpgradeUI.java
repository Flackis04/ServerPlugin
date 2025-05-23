package org.bear.serverPlugin.ui;

import org.bear.serverPlugin.data.PlayerData;
import org.bear.serverPlugin.utils.InventoryCoordinate;
import org.bear.serverPlugin.utils.InventoryCoordinateUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Map;

public class UpgradeUI extends UIBlueprint {
    private static InventoryCoordinate islandUpgradeCoordinate = InventoryCoordinateUtil.getCoordinateFromSlotIndex(10);
    private static InventoryCoordinate generatorUpgradeCoordinate = InventoryCoordinateUtil.getCoordinateFromSlotIndex(12);

    private final PlayerData playerData;

    public UpgradeUI(PlayerData playerData) {
        super(6, "Upgrades", false);
        this.playerData = playerData;
    }

    protected void updateInventory() {
        setSlotItem(createIslandUpgradeBtn(), islandUpgradeCoordinate);
        setSlotItem(createGenUpgradeMenuBtn(), generatorUpgradeCoordinate);
        setSlotItem(create);
    }

    protected boolean onSlotClick(Player player, InventoryCoordinate coordinate, ClickType clickType) {
        if (coordinate.isAt(islandUpgradeCoordinate)) {
            // island upgrade
        } else if (coordinate.isAt(generatorUpgradeCoordinate)) {
            // generator upgrade
        }

        return false;
    }

    public ItemStack createIslandUpgradeBtn() {
        int currentLevel = data.islandExpansionLevel;
        Map<Integer, Integer> costs = state.islandExpansionLevelCosts;  // islandLevelCosts map
        int cost = costs.getOrDefault(currentLevel, 0);

        ItemStack item = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "Island Expansion");

            String costText = currentLevel == state.maxIslandExpansionLevel  // Ensure max level is tracked
                    ? ChatColor.RED + "Max level reached"
                    : (data.crypto >= cost ? ChatColor.GREEN : ChatColor.RED) + "Cost: " + cost;

            meta.setLore(Arrays.asList(
                    ChatColor.DARK_GRAY + "Add a chunk to your island per level",
                    ChatColor.GRAY + "Level: " + currentLevel,  // Show current island level
                    costText
            ));

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }

    public ItemStack createMaxGeneratorsBtn() {
        int cost = costs.getOrDefault(playerData.maxGenerators, 0);

        ItemStack item = new ItemStack(Material.BARREL);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "Storage");

            String costText = playerData.maxGenerators == state.maxSlotLevel
                    ? ChatColor.RED + "Max level reached"
                    : (playerData.crypto >= cost ? ChatColor.GREEN : ChatColor.RED) + "Cost: " + cost;

            meta.setLore(Arrays.asList(
                    ChatColor.DARK_GRAY + "Enables you to place an additional Gen per level",
                    ChatColor.GRAY + "Level: " + playerData.maxGenerators,
                    costText
            ));

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }

    public ItemStack createGenUpgradeMenuBtn() {
        ItemStack item = new ItemStack(Material.IRON_BLOCK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "Generator Upgrades");

            meta.setLore(Arrays.asList(
                    ChatColor.DARK_GRAY + "Upgrade your Gens"
            ));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }
}
