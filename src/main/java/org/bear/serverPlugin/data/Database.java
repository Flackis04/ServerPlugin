package org.bear.serverPlugin.data;

import org.bear.serverPlugin.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Database {
    private Connection connection;

    public void connect() {
        try {
            // Create a connection to the database
            connection = DriverManager.getConnection("jdbc:sqlite:playerData.db");
            System.out.println("Connected to the SQLite database.");
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Disconnected from the database.");
            }
        } catch (SQLException e) {
            System.err.println("Disconnection failed: " + e.getMessage());
        }
    }

    public void createTable() {
        try (Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS player_data (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "crypto INTEGER," +
                    "delayLevel INTEGER," +
                    "slotLevel INTEGER," +
                    "islandExpansionLevel INTEGER," +
                    "genIsActive INTEGER," +
                    "genLocation_x REAL," +
                    "genLocation_y REAL," +
                    "genLocation_z REAL," +
                    "genLocation_world TEXT," +
                    "matInCollection TEXT," +
                    "inventory_items TEXT);";  // Added column for inventory items
            statement.executeUpdate(sql);
            System.out.println("Table created or already exists.");
        } catch (SQLException e) {
            System.err.println("Table creation failed: " + e.getMessage());
        }
    }

    public void insertOrUpdatePlayerData(int playerId, PlayerData playerData) {
        // SQL query to insert or update player data
        String sql = "INSERT INTO player_data (id, crypto, delayLevel, slotLevel, islandExpansionLevel, genIsActive, " +
                "genLocation_x, genLocation_y, genLocation_z, genLocation_world, matInCollection, inventory_items) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT(id) DO UPDATE SET " +
                "crypto = ?, delayLevel = ?, slotLevel = ?, islandExpansionLevel = ?, genIsActive = ?, " +
                "genLocation_x = ?, genLocation_y = ?, genLocation_z = ?, genLocation_world = ?, matInCollection = ?, inventory_items = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Set the values for insertion
            stmt.setInt(1, playerId);  // 'id' as playerId
            stmt.setInt(2, playerData.crypto);
            stmt.setInt(3, playerData.delayLevel);
            stmt.setInt(4, playerData.slotLevel);
            stmt.setInt(5, playerData.islandExpansionLevel);
            stmt.setInt(6, playerData.genIsActive ? 1 : 0);  // Store as 1 for true, 0 for false
            stmt.setDouble(7, playerData.getGenLocation().getX());
            stmt.setDouble(8, playerData.getGenLocation().getY());
            stmt.setDouble(9, playerData.getGenLocation().getZ());
            stmt.setString(10, playerData.getGenLocation().getWorld().getName());  // World name

            // Serialize Set<Material> into a comma-separated String
            String materialsString = String.join(",", playerData.matInCollection.stream()
                    .map(Material::name)
                    .toArray(String[]::new));
            stmt.setString(11, materialsString);

            // Serialize inventory items into a string (using InventoryUtils.serializeInventory())
            String serializedInventory = InventoryUtils.serializeInventory(playerData.getInventoryItems());
            stmt.setString(12, serializedInventory);

            // Set the values for updating (for the ON CONFLICT clause)
            stmt.setInt(13, playerData.crypto);
            stmt.setInt(14, playerData.delayLevel);
            stmt.setInt(15, playerData.slotLevel);
            stmt.setInt(16, playerData.islandExpansionLevel);
            stmt.setInt(17, playerData.genIsActive ? 1 : 0);
            stmt.setDouble(18, playerData.getGenLocation().getX());
            stmt.setDouble(19, playerData.getGenLocation().getY());
            stmt.setDouble(20, playerData.getGenLocation().getZ());
            stmt.setString(21, playerData.getGenLocation().getWorld().getName());
            stmt.setString(22, materialsString);
            stmt.setString(23, serializedInventory);  // Update inventory column

            // Execute the query
            stmt.executeUpdate();
            System.out.println("Player data inserted or updated.");
        } catch (SQLException e) {
            System.err.println("Insert/Update failed: " + e.getMessage());
        }
    }

    public PlayerData loadPlayerData(int playerId) {
        String sql = "SELECT * FROM player_data WHERE id = ?";
        PlayerData playerData = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, playerId);  // Set the player ID as a parameter

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Load data from the result set
                    int crypto = rs.getInt("crypto");
                    int delayLevel = rs.getInt("delayLevel");
                    int slotLevel = rs.getInt("slotLevel");
                    int islandExpansionLevel = rs.getInt("islandExpansionLevel");
                    boolean genIsActive = rs.getInt("genIsActive") == 1;

                    double genLocationX = rs.getDouble("genLocation_x");
                    double genLocationY = rs.getDouble("genLocation_y");
                    double genLocationZ = rs.getDouble("genLocation_z");
                    String genLocationWorld = rs.getString("genLocation_world");

                    // Deserialize materials collection
                    String materialsString = rs.getString("matInCollection");
                    Set<Material> materials = new HashSet<>();
                    if (materialsString != null && !materialsString.isEmpty()) {
                        String[] materialNames = materialsString.split(",");
                        for (String materialName : materialNames) {
                            try {
                                Material material = Material.getMaterial(materialName);
                                if (material != null) {
                                    materials.add(material);
                                } else {
                                    System.err.println("Invalid material: " + materialName); // Log invalid material names
                                }
                            } catch (Exception e) {
                                System.err.println("Error converting material: " + e.getMessage());
                            }
                        }
                    }

                    // Fetch serialized inventory from the database
                    String serializedInventory = rs.getString("inventory_items");  // This is the column that stores the serialized inventory

                    // Deserialize the inventory string into a list of ItemStacks
                    List<ItemStack> inventoryItems = null;
                    if (serializedInventory != null && !serializedInventory.isEmpty()) {
                        inventoryItems = InventoryUtils.deserializeInventory(serializedInventory);
                    }

                    // Create a new PlayerData object with the deserialized inventory and other data
                    playerData = new PlayerData(
                            crypto,
                            delayLevel,
                            slotLevel,
                            islandExpansionLevel,
                            genIsActive,
                            new Location(Bukkit.getWorld(genLocationWorld), genLocationX, genLocationY, genLocationZ),
                            materials,
                            inventoryItems != null ? inventoryItems : new ArrayList<>()  // Ensure we pass an empty list if inventory is null
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Load player data failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }

        return playerData;
    }
}
