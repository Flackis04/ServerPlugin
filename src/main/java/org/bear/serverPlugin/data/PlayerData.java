package org.bear.serverPlugin.data;

import org.bear.serverPlugin.utils.MaterialUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerData {

    // Player data fields
    public int crypto = 0;
    public int delayLevel = 1;
    public int slotLevel = 1;
    public int islandExpansionLevel = 1;
    public int multiplierLevel = 1;
    public boolean genIsActive = false;
    public int gensPlaced = 0;

    public Map<Material, Double> valuables;
    public Map<Material, Boolean> seenMaterials;
    public Map<Material, Integer> sellPrices;

    private final Set<Material> matInCollection = new HashSet<>();
    private Set<Location> genLocations = new HashSet<>();
    private List<ItemStack> inventoryItems = new ArrayList<>();

    // Default constructor (you can leave it empty or use it for default init)
    public PlayerData() {}

    // Constructor with parameters
    public PlayerData(int crypto, int delayLevel, int slotLevel, int islandExpansionLevel,
                      boolean genIsActive, Set<Location> genLocations,
                      Set<Material> matInCollection,
                      List<Material> orderedMats) {
        this.crypto = crypto;
        this.delayLevel = delayLevel;
        this.slotLevel = slotLevel;
        this.islandExpansionLevel = islandExpansionLevel;
        this.genIsActive = genIsActive;
        this.genLocations.addAll(genLocations);
        this.matInCollection.addAll(matInCollection);
        this.inventoryItems = new ArrayList<>(inventoryItems);

        // Initialize valuables, sellPrices, and seenMaterials using orderedMats
        this.valuables = MaterialUtils.generateExponentialWeights(orderedMats, 0.27, multiplierLevel);
        this.sellPrices = MaterialUtils.generateSellPrices(this.valuables, 10000);
        this.seenMaterials = MaterialUtils.generateInitialSeenMap(orderedMats);
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
