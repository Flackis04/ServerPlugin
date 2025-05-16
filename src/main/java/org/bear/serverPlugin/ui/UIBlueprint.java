package org.bear.serverPlugin.ui;

import net.kyori.adventure.text.Component;
import org.bear.serverPlugin.ServerPlugin;
import org.bear.serverPlugin.utils.InventoryCoordinate;
import org.bear.serverPlugin.utils.InventoryCoordinateUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class UIBlueprint implements InventoryHolder, Listener {
    private final Inventory inventory;
    private final boolean forceOpen;

    public UIBlueprint(int rows, String title, boolean forceOpen) {
        inventory = Bukkit.createInventory(this, rows * 9, Component.text(title));
        this.forceOpen = forceOpen;

        ServerPlugin.getPlugin().getServer().getPluginManager().registerEvents(this, ServerPlugin.getPlugin());

        new BukkitRunnable() {
            @Override
            public void run() {
                updateInventory();
            }
        }.runTaskLater(ServerPlugin.getPlugin(), 0);
    }

    abstract protected void updateInventory();

    /**
     * Listener for when a player clicks a slot.
     *
     * @param player     The player who clicked a slot
     * @param coordinate What slot the player clicked
     * @param clickType  How the player clicked the slot
     * @return true if the menu should be stopped, false if it should stay open after the method is called
     */
    abstract protected boolean onSlotClick(Player player, InventoryCoordinate coordinate, ClickType clickType);

    protected void setSlotItem(ItemStack itemStack, int x, int y) {
        assert x >= 0 && x < 9 && y >= 0 && y < inventory.getSize() / 9;
        inventory.setItem(y * 9 + x, itemStack);
    }

    protected void setSlotItem(ItemStack itemStack, InventoryCoordinate coordinate) {
        setSlotItem(itemStack, coordinate.getX(), coordinate.getY());
    }

    public void openInventory(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == inventory) {
            if (onSlotClick(
                    (Player) e.getWhoClicked(),
                    InventoryCoordinateUtil.getCoordinateFromSlotIndex(e.getSlot()),
                    e.getClick()
            ))
                stop();
        } else if (e.getInventory() != inventory)
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory() == inventory) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (forceOpen)
                        e.getPlayer().openInventory(e.getInventory());
                    else if (inventory.getViewers().isEmpty())
                        stop();
                }
            }.runTaskLater(ServerPlugin.getPlugin(), 0);
        }
    }

    protected void stop() {
        HandlerList.unregisterAll(this);

        for (HumanEntity humanEntity : inventory.getViewers())
            new BukkitRunnable() {
                @Override
                public void run() {
                    humanEntity.closeInventory();
                }
            }.runTaskLater(ServerPlugin.getPlugin(), 0);
    }
}