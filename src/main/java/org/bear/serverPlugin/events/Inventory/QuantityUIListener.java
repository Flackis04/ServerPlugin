package org.bear.serverPlugin.events.Inventory;

import org.bear.serverPlugin.data.PluginState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    // Constructor with PluginState passed
    public QuantityUIListener(PluginState state) {
        this.state = state;
    }

    // Set the selection for the player
    public void setSelection(Player player, Material material, int costPerItem) {
        selectedQuantities.put(player.getUniqueId(), 1); // Default to 1
        selectedMaterials.put(player.getUniqueId(), material);
        itemCosts.put(player.getUniqueId(), costPerItem);
    }

    @EventHandler
    public void onQuantityClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        // Check if the inventory title matches the quantity selection menu
        if (!title.equals("Select Quantity")) return;

        event.setCancelled(true); // Prevent item from being taken

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        UUID uuid = player.getUniqueId();
        int quantity = selectedQuantities.getOrDefault(uuid, 1);

        String name = clicked.getItemMeta().getDisplayName();
        if (name.contains("+1")) quantity += 1;
        else if (name.contains("+16")) quantity += 16;
        else if (name.contains("+64")) quantity += 64;
        else if (name.contains("-1")) quantity = Math.max(1, quantity - 1);
        else if (name.contains("-16")) quantity = Math.max(1, quantity - 16);
        else if (name.contains("-64")) quantity = Math.max(1, quantity - 64);
        else if (name.contains("Confirm")) {
            Material material = selectedMaterials.get(uuid);
            int cost = itemCosts.get(uuid) * quantity;

            // Placeholder for balance check
            if (canAfford(player, cost)) {
                // Subtract cost from the player's balance
                subtractBalance(player, cost);

                ItemStack result = new ItemStack(material, quantity);
                player.getInventory().addItem(result);
                player.sendMessage("§aPurchased " + quantity + " " + material.name() + " for " + cost + " crypto.");
                player.closeInventory();
            } else {
                player.sendMessage("§cYou do not have enough crypto.");
            }
            return;
        } else if (name.contains("Cancel")) {
            player.closeInventory();
            return;
        }

        // Update the selected quantity
        selectedQuantities.put(uuid, quantity);
        player.sendMessage("§eSelected Quantity: §f" + quantity);

        // Optionally, update the inventory UI with the new quantity
        // You can implement this depending on how you want the quantity to be updated in the UI.
    }

    // Placeholder method to check if the player can afford the item
    private boolean canAfford(Player player, int cost) {
        // Replace this with actual balance check logic (e.g., from a database or player data)
        return true; // Always returns true for now
    }

    // Placeholder method to subtract the cost from the player's balance
    private void subtractBalance(Player player, int cost) {
        // Replace this with actual logic for subtracting from the player's balance
        // For example, using player data or an economy plugin
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        // Check if the player clicked in the inventory (not the hotbar, etc.)
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        // You can check for the title of the inventory (if it has one) or check the item clicked
        if (event.getClickedInventory() == null) return;

        // Assuming you have an inventory with specific items you want to handle
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String itemName = clickedItem.getItemMeta().getDisplayName();

        // Check if the clicked item is one that should trigger the quantity UI
        if (itemName.equals(ChatColor.YELLOW + "Dirt")) { // Replace with your item name or condition
            event.setCancelled(true);  // Cancel the default behavior (such as picking up the item)

            // Open the quantity UI
            openQuantityUI(player, clickedItem);
        }
    }

    private void openQuantityUI(Player player, ItemStack clickedItem) {
        // Example of setting the material and cost for the selected item
        Material material = clickedItem.getType();
        int costPerItem = 10; // Set the cost of the item here (you can make it dynamic)

        // Set the player's selection (material and cost)
        QuantityUIListener quantityUIListener = new QuantityUIListener(state); // Make sure state is initialized properly
        quantityUIListener.setSelection(player, material, costPerItem);

        // Open the UI for selecting quantity (you'll need to create a custom inventory for this)
        player.openInventory(createQuantityInventory());
    }

    // Method to create the inventory for selecting quantity
    private Inventory createQuantityInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, "Select Quantity");

        // Add items to the inventory to allow the player to select quantities, confirm or cancel
        ItemStack plusOne = new ItemStack(Material.GREEN_WOOL);
        ItemMeta plusOneMeta = plusOne.getItemMeta();
        plusOneMeta.setDisplayName("+1");
        plusOne.setItemMeta(plusOneMeta);
        inventory.addItem(plusOne);

        // Add other buttons for -1, +16, etc.

        // Add a confirm and cancel button
        ItemStack confirmButton = new ItemStack(Material.DIAMOND_BLOCK);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        confirmMeta.setDisplayName("Confirm");
        confirmButton.setItemMeta(confirmMeta);
        inventory.addItem(confirmButton);

        ItemStack cancelButton = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta cancelMeta = cancelButton.getItemMeta();
        cancelMeta.setDisplayName("Cancel");
        cancelButton.setItemMeta(cancelMeta);
        inventory.addItem(cancelButton);

        return inventory;
    }

}
