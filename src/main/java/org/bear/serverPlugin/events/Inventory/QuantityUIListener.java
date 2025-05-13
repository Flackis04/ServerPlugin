package org.bear.serverPlugin.events.Inventory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bear.serverPlugin.data.PluginState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuantityUIListener implements Listener {

    private final Map<UUID, Integer> selectedQuantities = new HashMap<>();
    private final Map<UUID, Material> selectedMaterials = new HashMap<>();
    private final Map<UUID, Integer> itemCosts = new HashMap<>();

    private final PluginState state;

    public QuantityUIListener(PluginState state) {
        this.state = state;
    }

    public void setSelection(Player player, Material material, int costPerItem) {
        selectedQuantities.put(player.getUniqueId(), 1);
        selectedMaterials.put(player.getUniqueId(), material);
        itemCosts.put(player.getUniqueId(), costPerItem);
    }

    @EventHandler
    public void onQuantityClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getView().title() instanceof TextComponent title) || !title.content().equals("Select Quantity")) return;

        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        UUID uuid = player.getUniqueId();
        int quantity = selectedQuantities.getOrDefault(uuid, 1);
        String name = clicked.getItemMeta().displayName() instanceof TextComponent tc ? tc.content() : "";

        quantity = switch (name) {
            case "+1" -> Math.min(64, quantity + 1);
            case "+9" -> Math.min(64, quantity + 10);
            case "+64" -> 64;
            case "-1" -> Math.max(1, quantity - 1);
            case "-10" -> Math.max(1, quantity - 10);
            case "-64" -> Math.max(1, quantity - 64);
            case "Confirm" -> {
                handleConfirm(player, uuid, quantity);
                yield quantity;
            }
            case "Cancel" -> {
                player.closeInventory();
                yield quantity;
            }
            default -> quantity;
        };

        selectedQuantities.put(uuid, quantity);
        state.marketUI.openQuantityUI(player, selectedMaterials.get(uuid), itemCosts.get(uuid), quantity);
        player.sendMessage(Component.text("Selected Quantity: " + quantity).color(NamedTextColor.YELLOW));
    }

    private void handleConfirm(Player player, UUID uuid, int quantity) {
        Material material = selectedMaterials.get(uuid);
        int cost = itemCosts.get(uuid) * quantity;

        if (canAfford(player, cost)) {
            subtractBalance(player, cost);
            state.scoreboardManager.updateCrypto(player, state.getPlayerData(player.getUniqueId()).crypto);
            player.getInventory().addItem(new ItemStack(material, quantity));
            player.sendMessage(Component.text("Purchased " + quantity + " " + material.name() + " for " + cost + " crypto.").color(NamedTextColor.GREEN));
            player.closeInventory();
        } else {
            player.sendMessage(Component.text("You do not have enough crypto.").color(NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getView().title() instanceof TextComponent title) || !title.content().equals("Market")) return;

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String itemName = clickedItem.getItemMeta().displayName() instanceof TextComponent tc ? tc.content() : "";

        for (ItemStack item : event.getClickedInventory().getContents()) {
            if (item == null || !item.hasItemMeta()) continue;

            String marketItemName = item.getItemMeta().displayName() instanceof TextComponent tcItem ? tcItem.content() : "";

            if (!itemName.equals(marketItemName)) continue;
            event.setCancelled(true);
            setSelection(player, clickedItem.getType(), getCostForItem(clickedItem.getType()));
            player.openInventory(state.marketUI.createQuantityInventory(clickedItem.getType()));
            break;
        }
    }

    // Example method to get cost for a material
    private int getCostForItem(Material material) {
        switch (material) {
            case DIRT:
                return 2;
            case WOODEN_AXE:
                return 100;
            // Add more cases as needed
            default:
                return 0;
        }
    }

    private boolean canAfford(Player player, int cost) {
        return state.getPlayerData(player.getUniqueId()).crypto >= cost;
    }

    private void subtractBalance(Player player, int cost) {
        state.getPlayerData(player.getUniqueId()).crypto -= cost;
    }
}
