package org.bear.serverPlugin.data;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerData {
    public int crypto = 0;
    public int delayLevel = 1;
    public int slotLevel = 1;
    public int islandExpansionLevel = 1;
    public boolean genIsActive = false;

    // Store materials in collection
    public final Set<Material> matInCollection = new HashSet<>();

    // Store generator location per player
    private Location genLocation;

    // Store player's inventory items (new field)
    private List<ItemStack> inventoryItems = new ArrayList<>();

    // Default constructor
    public PlayerData() {
        this.genLocation = null; // Explicitly set to null to avoid unintentional uninitialized state
    }

    // Constructor for initializing PlayerData with parameters
    public PlayerData(int crypto, int delayLevel, int slotLevel, int islandExpansionLevel, boolean genIsActive, Location genLocation, Set<Material> matInCollection, List<ItemStack> inventoryItems) {
        this.crypto = crypto;
        this.delayLevel = delayLevel;
        this.slotLevel = slotLevel;
        this.islandExpansionLevel = islandExpansionLevel;
        this.genIsActive = genIsActive;
        this.genLocation = genLocation;
        this.matInCollection.addAll(matInCollection);
        this.inventoryItems = inventoryItems;  // Set inventory items
    }

    // Setter and getter for genLocation
    public void setGenLocation(Location location) {
        this.genLocation = location;
    }

    public Location getGenLocation() {
        return genLocation;
    }

    // Method to add materials to the collection
    public void addMaterialToCollection(Material material) {
        matInCollection.add(material);
    }

    // Method to remove materials from the collection
    public void removeMaterialFromCollection(Material material) {
        matInCollection.remove(material);
    }

    // Method to check if a material is in the collection
    public boolean hasMaterial(Material material) {
        return matInCollection.contains(material);
    }

    // Get the current materials in the collection
    public Set<Material> getMatInCollection() {
        return matInCollection;
    }

    // Getter and setter for inventory items
    public List<ItemStack> getInventoryItems() {
        return inventoryItems;
    }

    public void setInventoryItems(List<ItemStack> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    // Method to add an item to the player's inventory
    public void addItemToInventory(ItemStack item) {
        this.inventoryItems.add(item);
    }

    // Method to remove an item from the player's inventory
    public void removeItemFromInventory(ItemStack item) {
        this.inventoryItems.remove(item);
    }

    // Override toString for easy debugging
    @Override
    public String toString() {
        return "PlayerData{" +
                "crypto=" + crypto +
                ", delayLevel=" + delayLevel +
                ", slotLevel=" + slotLevel +
                ", islandExpansionLevel=" + islandExpansionLevel +
                ", genIsActive=" + genIsActive +
                ", genLocation=" + genLocation +
                ", matInCollection=" + matInCollection +
                ", inventoryItems=" + inventoryItems +
                '}';
    }
}
