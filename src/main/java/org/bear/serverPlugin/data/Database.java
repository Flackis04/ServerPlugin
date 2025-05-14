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
        String createPlayersTable = "CREATE TABLE IF NOT EXISTS players (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "crypto INTEGER," +
                "delayLevel INTEGER," +
                "slotLevel INTEGER," +
                "islandExpansionLevel INTEGER," +
                "matInCollection TEXT" +
                ");";

        String createGeneratorsTable = "CREATE TABLE IF NOT EXISTS generators (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "block TEXT" +
                ");";

        String createLocationsTable = "CREATE TABLE IF NOT EXISTS locations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "x INTEGER," +
                "y INTEGER," +
                "z INTEGER," +
                "worldName TEXT" +
                ");";

        String createPlayerGeneratorsTable = "CREATE TABLE IF NOT EXISTS playergenerators (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "location INTEGER," +
                "generator INTEGER," +
                "player INTEGER," +
                "FOREIGN KEY (player) REFERENCES players (id)," +
                "FOREIGN KEY (location) REFERENCES locations (id)," +
                "FOREIGN KEY (generator) REFERENCES generators (id)" +
                ");";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createPlayersTable);
            statement.executeUpdate(createGeneratorsTable);
            statement.executeUpdate(createLocationsTable);
            statement.executeUpdate(createPlayerGeneratorsTable);
            System.out.println("All tables created or already exist.");
        } catch (SQLException e) {
            System.err.println("Table creation failed: " + e.getMessage());
        }
    }


    public void insertPlayerData(int playerId) {
        String sql = "INSERT OR IGNORE INTO players (id, crypto, delayLevel, slotLevel, islandExpansionLevel, matInCollection) " +
                "VALUES (?, 0, 1, 1, 1, '')";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, playerId);
            stmt.executeUpdate();
            System.out.println("Player data inserted.");
        } catch (SQLException e) {
            System.err.println("Insert failed: " + e.getMessage());
        }
    }


    public void updatePlayerData(int playerId, PlayerData playerData) {
        String sql = "UPDATE players SET " +
                "crypto = ?, delayLevel = ?, slotLevel = ?, islandExpansionLevel = ?, " +
                "matInCollection = ? " +
                "WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, playerData.crypto);
            stmt.setInt(2, playerData.delayLevel);
            stmt.setInt(3, playerData.slotLevel);
            stmt.setInt(4, playerData.islandExpansionLevel);

            String materialsString = String.join(",", playerData.getMatInCollection().stream()
                    .map(Material::name)
                    .toArray(String[]::new));

            stmt.setString(5, materialsString);
            stmt.setInt(6, playerId);

            stmt.executeUpdate();
            System.out.println("Player data updated.");
        } catch (SQLException e) {
            System.err.println("Update failed: " + e.getMessage());
        }
    }


    public PlayerData loadPlayerData(int playerId) {
        String sql = "SELECT * FROM players WHERE id = ?";
        PlayerData playerData = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, playerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int crypto = rs.getInt("crypto");
                    int delayLevel = rs.getInt("delayLevel");
                    int slotLevel = rs.getInt("slotLevel");
                    int islandExpansionLevel = rs.getInt("islandExpansionLevel");

                    Set<Material> materials = new HashSet<>();
                    String materialsString = rs.getString("matInCollection");

                    if (materialsString != null && !materialsString.isEmpty()) {
                        String[] materialNames = materialsString.split(",");
                        for (String name : materialNames) {
                            Material material = Material.getMaterial(name);
                            if (material != null) {
                                materials.add(material);
                            } else {
                                System.err.println("Unknown material in DB: " + name);
                            }
                        }
                    }

                    playerData = new PlayerData(
                            crypto,
                            delayLevel,
                            slotLevel,
                            islandExpansionLevel,
                            true,
                            new HashSet<>(),  // Placeholder for genLocations
                            materials,
                            new ArrayList<>() // Placeholder for inventoryItems
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Load player data failed: " + e.getMessage());
        }

        return playerData;
    }
}
