package org.bear.serverPlugin.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.sql.*;
import java.util.*;

import static java.lang.String.join;

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
        String statements = "";
        statements += "CREATE TABLE IF NOT EXISTS players (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "crypto INTEGER DEFAULT 0," +
                "maxGenerators INTEGER DEFAULT 0," +
                "multiplier INTEGER DEFAULT 0," +
                ");";

        statements += "CREATE TABLE IF NOT EXISTS materials (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT" +
                ");";

        statements += "CREATE TABLE IF NOT EXISTS playerSeenMaterials (" +
                "player INTEGER PRIMARY KEY," +
                "material INTEGER PRIMARY KEY," +
                "FOREIGN KEY (player) REFERENCES players (id)," +
                "FOREIGN KEY (material) REFERENCES materials (id)," +
                ");";

        statements += "CREATE TABLE IF NOT EXISTS islands (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "player INTEGER," +
                "expansionLevel INTEGER," +
                "FOREIGN KEY (player) REFERENCES players (id)," +
                ");";

        statements += "CREATE TABLE IF NOT EXISTS generators (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "block TEXT," +
                join(',', Arrays.stream(PlayerGenerator.Trait.values()).map(t -> t.columnPrefix + "Cost INTEGER").toList()) +
                join(',', Arrays.stream(PlayerGenerator.Trait.values()).map(t -> t.columnPrefix + "MaxLevel INTEGER").toList()) +
                ");";

        statements += "CREATE TABLE IF NOT EXISTS locations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "x INTEGER," +
                "y INTEGER," +
                "z INTEGER," +
                "worldName TEXT" +
                ");";

        statements += "CREATE TABLE IF NOT EXISTS islandChunks (" +
                "island INTEGER PRIMARY KEY," +
                "location INTEGER PRIMARY KEY," +
                "FOREIGN KEY (island) REFERENCES islands (id)," +
                "FOREIGN KEY (location) REFERENCES locations (id)," +
                ");";

        statements += "CREATE TABLE IF NOT EXISTS playerGenerators (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "location INTEGER," +
                "generator INTEGER," +
                "player INTEGER," +
                join(',', Arrays.stream(PlayerGenerator.Trait.values()).map(t -> t.columnPrefix + "Level INTEGER DEFAULT 0").toList()) +
                "FOREIGN KEY (player) REFERENCES players (id)," +
                "FOREIGN KEY (location) REFERENCES locations (id)," +
                "FOREIGN KEY (generator) REFERENCES generators (id)" +
                ");";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(statements);
            System.out.println("All tables created or already exist.");
        } catch (SQLException e) {
            System.err.println("Table creation failed: " + e.getMessage());
        }
    }


    public void insertPlayerData(UUID playerUuid) {
        String sql = "INSERT OR IGNORE INTO players (id) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, playerUuid.hashCode());
            stmt.executeUpdate();
            System.out.println("Player data inserted.");
        } catch (SQLException e) {
            System.err.println("Insert failed: " + e.getMessage());
        }
    }


    public void updatePlayerData(int playerId, PlayerData playerData) {
        String sql = "UPDATE players SET crypto = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, playerData.crypto);
            stmt.setInt(2, playerId);

            //TODO: add seenmaterials
            //TODO: add playergenerators
            //TODO: etc...

            stmt.executeUpdate();
            System.out.println("Player data updated.");
        } catch (SQLException e) {
            System.err.println("Update failed: " + e.getMessage());
        }
    }


    public PlayerData loadPlayerData(UUID playerUuid) {
        PlayerData playerData = new PlayerData();

        String playerDataStatement = "SELECT * FROM players WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(playerDataStatement)) {
            stmt.setInt(1, playerUuid.hashCode());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    playerData.crypto = rs.getInt("crypto");
                }
            }
        } catch (SQLException e) {
            System.err.println("Load player data failed: " + e.getMessage());
        }

        String playerSeenMaterialsStatement = "SELECT * FROM playerSeenMaterials WHERE player = ? " +
                "JOIN materials ON materials.id = material";

        try (PreparedStatement stmt = connection.prepareStatement(playerSeenMaterialsStatement)) {
            stmt.setInt(1, playerUuid.hashCode());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    playerData.seenMaterials.add(Material.getMaterial(rs.getString("materials.name")));
            }
        } catch (SQLException e) {
            System.err.println("Load player seen materials failed: " + e.getMessage());
        }

        return playerData;
    }

    public Map<Integer, List<PlayerGenerator>> getAllGenerators() {
        Map<Integer, List<PlayerGenerator>> playerGenerators = new HashMap<>();

        String playerGeneratorsStatement = "SELECT * FROM playerGenerators " +
                "JOIN generators ON generators.id = generator " +
                "JOIN locations ON locations.id = location";

        try (PreparedStatement stmt = connection.prepareStatement(playerGeneratorsStatement)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PlayerGenerator generator = new PlayerGenerator();

                    generator.location = rs.getObject("locations.id") != null ? new Location(
                            Bukkit.getServer().getWorld(rs.getString("locations.worldName")),
                            rs.getInt("locations.x"),
                            rs.getInt("locations.y"),
                            rs.getInt("locations.z")
                    ) : null;

                    for (PlayerGenerator.Trait trait : PlayerGenerator.Trait.values())
                        generator.traits.put(trait, new PlayerGenerator.TraitData(
                                rs.getInt(trait.columnPrefix + "Level"),
                                rs.getInt("generators." + trait.columnPrefix + "Cost"),
                                rs.getInt("generators." + trait.columnPrefix + "MaxLevel")
                        ));

                    int playerId = rs.getInt("player");
                    playerGenerators.putIfAbsent(playerId, new LinkedList<>());
                    playerGenerators.get(playerId).add(generator);
                }
            }
        } catch (SQLException e) {
            System.err.println("Load player generator data failed: " + e.getMessage());
        }

        return playerGenerators;
    }

    //TODO implement
    public Set<Island> playerIslands(){
        return new HashSet<>();
    }

    /*
            String playerIslandsStatement = "SELECT * FROM islands WHERE player = ?";
        try (PreparedStatement stmt = connection.prepareStatement(playerIslandsStatement)) {
            stmt.setInt(1, playerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String playerIslandsStatement = "SELECT * FROM islands WHERE player = ?";
                    try (PreparedStatement stmt = connection.prepareStatement(playerIslandsStatement)) {
                        stmt.setInt(1, playerId);

                        try (ResultSet rs = stmt.executeQuery()) {
                            while (rs.next()) {
                                playerData.seenMaterials.add(Material.getMaterial(rs.getString("materials.name")));

                            }
                        }
                    } catch (SQLException e) {
                        System.err.println("Load player islands failed: " + e.getMessage());
                    }

                }
            }
        } catch (SQLException e) {
            System.err.println("Load player islands failed: " + e.getMessage());
        }
     */
}
