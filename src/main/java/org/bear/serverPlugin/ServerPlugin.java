package org.bear.serverPlugin;

import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.events.*;
import org.bear.serverPlugin.ui.*;
import org.bear.serverPlugin.world.ChunkIsland;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Initialize the ScoreboardManager
        ScoreboardManager scoreboardManager = new ScoreboardManager();

        // Step 1: Create an empty PluginState with null UIs (just for now)
        PluginState state = new PluginState(null, null, null, null, scoreboardManager);

        // Step 2: Now initialize all UI components using the full state
        UpgradeUI upgradeUI = new UpgradeUI(state);
        SellUI sellUI = new SellUI();
        CollectionUI collectionUI = new CollectionUI(state);
        PhoneUI phoneUI = new PhoneUI(state);

        // Step 3: Set those UIs back into the PluginState
        state.upgradeUI = upgradeUI;
        state.sellUI = sellUI;
        state.collectionUI = collectionUI;
        state.phoneUI = phoneUI;

        // Step 4: Register events using the complete state
        Bukkit.getPluginManager().registerEvents(new JoinListener(state), this);
        Bukkit.getPluginManager().registerEvents(new InteractListener(state), this);
        Bukkit.getPluginManager().registerEvents(new UIListener(state), this);
        Bukkit.getPluginManager().registerEvents(new GenListener(state), this);

        // Register commands
        getCommand("is").setExecutor(new ChunkIsland());

        getLogger().info("ServerPlugin enabled on Minecraft 1.21");
    }
}
