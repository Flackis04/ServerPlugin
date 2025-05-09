package org.bear.serverPlugin;

import org.bear.serverPlugin.data.Database;
import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.events.*;
import org.bear.serverPlugin.ui.*;
import org.bear.serverPlugin.world.ChunkIsland;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerPlugin extends JavaPlugin {
    private Database database;

    @Override
    public void onEnable() {
        // Initialize the ScoreboardManager
        ScoreboardManager scoreboardManager = new ScoreboardManager();

        database = new Database();
        database.connect();  // Connect to the database
        database.createTable();  // Create the table if it doesn't exist

        // Step 1: Create an empty PluginState with null UIs (just for now)
        PluginState state = new PluginState(null, null, null, null, scoreboardManager, database);

        // Step 3: Set those UIs back into the PluginStat
        state.upgradeUI = new UpgradeUI(state);
        state.sellUI = new SellUI();
        state.collectionUI = new CollectionUI(state);
        state.phoneUI = new PhoneUI(state);

        // Step 4: Register events using the complete state
        Bukkit.getPluginManager().registerEvents(new JoinListener(state, database), this);
        Bukkit.getPluginManager().registerEvents(new InteractListener(state), this);
        Bukkit.getPluginManager().registerEvents(new UIListener(state), this);
        Bukkit.getPluginManager().registerEvents(new GenListener(state), this);

        // Register commands
        getCommand("is").setExecutor(new ChunkIsland());

        getLogger().info("ServerPlugin enabled on Minecraft 1.21");
    }
    @Override
    public void onDisable() {
        // Ensure to disconnect when the plugin is disabled
        if (database != null) {
            database.disconnect();
        }
    }
}
