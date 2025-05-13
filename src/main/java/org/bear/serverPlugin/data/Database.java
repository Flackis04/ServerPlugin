package org.bear.serverPlugin.data;

import org.bear.serverPlugin.utils.GenUtils;
import org.bear.serverPlugin.utils.InventoryUtils;
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

    public void createTables() {
        try (Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS players (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "crypto INTEGER," +
                    "delayLevel INTEGER," +
                    "slotLevel INTEGER," +
                    "islandExpansionLevel INTEGER," +
                    "matInCollection TEXT," +
                    "inventory_items TEXT);" +

                    "CREATE TABLE IF NOT EXISTS generators (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "block TEXT);" +


                    "CREATE TABLE IF NOT EXISTS locations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "x INTEGER," +
                    "y INTEGER," +
                    "z INTEGER," +
                    "worldName TEXT);" +

                    "CREATE TABLE IF NOT EXISTS playergenerators (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "location INTEGER," +
                    "generator INTEGER," +
                    "player INTEGER," +
                    "FOREIGN KEY (player) REFERENCES players (id)," +
                    "FOREIGN KEY (location) REFERENCES locations (id)," +
                    "FOREIGN KEY (generator) REFERENCES generators (id));";


                    // Added column for inventory items
            statement.executeUpdate(sql);
            System.out.println("Table created or already exists.");
        } catch (SQLException e) {
            System.err.println("Table creation failed: " + e.getMessage());
        }
    }

    public void insertPlayerData(int playerId) {
        String sql = "INSERT OR IGNORE INTO players (id, crypto, delayLevel, slotLevel, islandExpansionLevel, genIsActive, " +
                "genLocations, matInCollection, inventory_items) " +
                "VALUES (?, 0, 1, 1, 1, 0, null, null, null)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Set the values for insertion
            stmt.setInt(1, playerId);  // 'id' as playerId

            stmt.executeUpdate();
            System.out.println("Player data inserted.");
        } catch (SQLException e) {
            System.err.println("Insert failed: " + e.getMessage());
        }
    }

    public void updatePlayerData(int playerId, PlayerData playerData) {
        // SQL query to insert or update player data
        String sql = "UPDATE players SET " +
                "crypto = ?, delayLevel = ?, slotLevel = ?, islandExpansionLevel = ?, genIsActive = ?, " +
                "genLocations = ?, matInCollection = ?, inventory_items = ? " +
                "WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, playerData.crypto);
            stmt.setInt(2, playerData.delayLevel);
            stmt.setInt(3, playerData.slotLevel);
            stmt.setInt(4, playerData.islandExpansionLevel);
            stmt.setInt(5, playerData.genIsActive ? 1 : 0);

            String materialsString = String.join(",", playerData.getMatInCollection().stream()
                    .map(Material::name)
                    .toArray(String[]::new));
            stmt.setString(7, materialsString);

            String serializedInventory = InventoryUtils.serializeInventory(playerData.getInventoryItems());
            stmt.setString(8, serializedInventory);
            stmt.setInt(9, playerId);

            stmt.executeUpdate();
            System.out.println("Player data inserted or updated.");
        } catch (SQLException e) {
            System.err.println("Insert/Update failed: " + e.getMessage());
        }
    }

    public PlayerData loadPlayerData(int playerId) {
        String sql = "SELECT * FROM players WHERE id = ?";
        PlayerData playerData = new PlayerData();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, playerId);  // Set the player ID as a parameter

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Load data from the result set
                    int crypto = rs.getInt("crypto");
                    int delayLevel = rs.getInt("delayLevel");
                    int slotLevel = rs.getInt("slotLevel");
                    int islandExpansionLevel = rs.getInt("islandExpansionLevel");


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

                    // Fetch the serialized genLocations from the database

                    playerData = new PlayerData(
                            crypto,
                            delayLevel,
                            slotLevel,
                            islandExpansionLevel,
                            true,
                            new HashSet<>(),
                            materials,
                            inventoryItems != null ? inventoryItems : new ArrayList<>()  // Ensure we pass an empty list if inventory is null
                    );

                }
            }
        } catch (SQLException e) {
            System.err.println("Load player data failed: " + e.getMessage());
        } catch (Exception e) {
            throw e;
        }

        return playerData;
    }
}
