package org.bear.serverPlugin.data;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerData {
    public int crypto = 0;
    public int delayLevel = 1;
    public int slotLevel = 1;
    public int islandExpansionLevel = 1;
    public boolean genIsActive = false;
    public int gensPlaced = 0;

    // Store materials in collection
    private final Set<Material> matInCollection = new HashSet<>();

    // Store generator locations per player
    private Set<Location> genLocations = new HashSet<>();

    // Store player's inventory items
    private List<ItemStack> inventoryItems = new ArrayList<>();

    // Default constructor
    public PlayerData() {}

    // Constructor with parameters
    public PlayerData(int crypto, int delayLevel, int slotLevel, int islandExpansionLevel,
                      boolean genIsActive, Set<Location> genLocations,
                      Set<Material> matInCollection, List<ItemStack> inventoryItems) {
        this.crypto = crypto;
        this.delayLevel = delayLevel;
        this.slotLevel = slotLevel;
        this.islandExpansionLevel = islandExpansionLevel;
        this.genIsActive = genIsActive;
        this.genLocations.addAll(genLocations);
        this.matInCollection.addAll(matInCollection);
        this.inventoryItems = new ArrayList<>(inventoryItems);
    }

    // Add a gen location
    public void addGenLocation(Location location) {
        this.genLocations.add(location);
    }

    // Remove a gen location
    public void removeGenLocation(Location location) {
        this.genLocations.remove(location);
    }

    // Set gen locations
    public void setGenLocations(Set<Location> genLocations) {
        if (genLocations != null) {
            this.genLocations = genLocations;
        } else {
            this.genLocations = new HashSet<>();
        }
    }

    // Get all gen locations
    public Set<Location> getGenLocations() {
        return genLocations;
    }

    // Material collection methods
    public void addMaterialToCollection(Material material) {
        matInCollection.add(material);
    }

    public void removeMaterialFromCollection(Material material) {
        matInCollection.remove(material);
    }

    public boolean hasMaterial(Material material) {
        return matInCollection.contains(material);
    }

    public Set<Material> getMatInCollection() {
        return matInCollection;
    }

    // Inventory methods
    public List<ItemStack> getInventoryItems() {
        return inventoryItems;
    }

    public void setInventoryItems(List<ItemStack> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    public void addItemToInventory(ItemStack item) {
        this.inventoryItems.add(item);
    }

    public void removeItemFromInventory(ItemStack item) {
        this.inventoryItems.remove(item);
    }

    // Reset all fields
    public void reset() {
        crypto = 0;
        delayLevel = 1;
        slotLevel = 1;
        islandExpansionLevel = 1;
        genIsActive = false;
        genLocations.clear();
        matInCollection.clear();
        inventoryItems.clear();
    }

    @Override
    public String toString() {
        return "PlayerData{" +
                "crypto=" + crypto +
                ", delayLevel=" + delayLevel +
                ", slotLevel=" + slotLevel +
                ", islandExpansionLevel=" + islandExpansionLevel +
                ", genIsActive=" + genIsActive +
                ", genLocations=" + genLocations +
                ", matInCollection=" + matInCollection +
                ", inventoryItems=" + inventoryItems +
                '}';
    }
}
