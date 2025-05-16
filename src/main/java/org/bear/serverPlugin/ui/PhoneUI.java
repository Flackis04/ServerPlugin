package org.bear.serverPlugin.ui;

import org.bear.serverPlugin.utils.InventoryCoordinate;
import org.bear.serverPlugin.utils.InventoryCoordinateUtil;
import org.bear.serverPlugin.utils.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class PhoneUI extends UIBlueprint{
    private static InventoryCoordinate upgradesCoordinate = InventoryCoordinateUtil.getCoordinateFromSlotIndex(10);
    private static InventoryCoordinate sellCoordinate = InventoryCoordinateUtil.getCoordinateFromSlotIndex(12);
    private static InventoryCoordinate collectionCoordinate = InventoryCoordinateUtil.getCoordinateFromSlotIndex(14);
    private static InventoryCoordinate marketCoordinate = InventoryCoordinateUtil.getCoordinateFromSlotIndex(16);

    public PhoneUI() {
        super(6, "Phone", false);
    }

    protected void updateInventory() {
        setSlotItem(ItemUtils.Button(Material.DIRT, "Upgrades", "Manage Upgrades"), upgradesCoordinate);
        setSlotItem(ItemUtils.Button(Material.GOLD_BLOCK, "Sell", "Sell duplicates"), sellCoordinate);
        setSlotItem(createCollectionBtn(Material.BOOK, "Collection", "Blocks you've discovered"), collectionCoordinate);
        setSlotItem(createMarketBtn(Material.DIAMOND_BLOCK, "Market", "Buy stuff here"), marketCoordinate);
    }

    protected boolean onSlotClick(Player player, InventoryCoordinate coordinate, ClickType clickType){
        if (coordinate.isAt(upgradesCoordinate)) {

        } else if (coordinate.isAt(sellCoordinate)) {

        } else if (coordinate.isAt(collectionCoordinate)) {

        } else if(coordinate.isAt(marketCoordinate)) {

        }

        return false;
    }
}
