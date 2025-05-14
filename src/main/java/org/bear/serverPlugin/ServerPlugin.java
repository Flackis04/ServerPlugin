package org.bear.serverPlugin;

import org.bear.serverPlugin.data.Database;
import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.events.*;
import org.bear.serverPlugin.events.Inventory.*;
import org.bear.serverPlugin.events.Inventory.QuantityUIListener;
import org.bear.serverPlugin.ui.*;
import org.bear.serverPlugin.commands.ChunkIsland;
import org.bear.serverPlugin.commands.Gens;
import org.bear.serverPlugin.world.DecayConcept;
import org.bear.serverPlugin.world.GenManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerPlugin extends JavaPlugin {
    private static ServerPlugin plugin;
    private Database database;
    public static ServerPlugin getPlugin(){
        return plugin;
    }
    @Override
    public void onEnable() {
        plugin = this;
        // Initialize the ScoreboardManager
        ScoreboardManager scoreboardManager = new ScoreboardManager();

        database = new Database();
        database.connect();  // Connect to the database
        database.createTables();  // Create the table if it doesn't exist

        // Step 1: Create an empty PluginState with null UIs (just for now)
        PluginState state = new PluginState(null, null, null, null, null, null, null, null, scoreboardManager, database);
        GenManager gen = new GenManager(state);

        // Step 3: Set those UIs back into the PluginState
        state.upgradeUI = new UpgradeUI(state);
        state.marketUI = new MarketUI(state);
        state.collectionUI = new CollectionUI(state);
        state.phoneUI = new PhoneUI(state);
        state.genUI = new GenUI(state);
        state.genMenuUI = new GenMenuUI(state);
        state.sellUI = new SellUI();

        // Step 4: Register events using the complete state
        Bukkit.getPluginManager().registerEvents(new PlayerListener(state, database), this);
        Bukkit.getPluginManager().registerEvents(new InteractListener(state, gen), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(state, gen), this);
        Bukkit.getPluginManager().registerEvents(new DecayConcept(), this);

        Bukkit.getPluginManager().registerEvents(new UIListener(state), this);
        Bukkit.getPluginManager().registerEvents(new QuantityUIListener(state), this); // ✅ Add this line
        Bukkit.getPluginManager().registerEvents(new SellUIListener(state), this);
        Bukkit.getPluginManager().registerEvents(new UpgradeUIListener(state), this);

        // Register commands
        getCommand("is").setExecutor(new ChunkIsland());
        getCommand("getgens").setExecutor(new Gens(state, gen));

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
