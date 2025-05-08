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
        // Initialize the ScoreboardManager first
        ScoreboardManager scoreboardManager = new ScoreboardManager();

        // Initialize the PluginState with temporary null values (no UI components yet)
        PluginState state = new PluginState(null, null, null, scoreboardManager);

        // Initialize UI components now that PluginState exists
        UpgradeUI upgradeUI = new UpgradeUI(state);
        SellUI sellUI = new SellUI();
        CollectionUI collectionUI = new CollectionUI(state);
        PhoneUI phoneUI = new PhoneUI(state);

        // Now, update the PluginState with the actual UI components
        state.upgradeUI = upgradeUI;
        state.sellUI = sellUI;
        state.collectionUI = collectionUI;
        state.phoneUI = phoneUI;

        // Register event listeners with the updated PluginState
        Bukkit.getPluginManager().registerEvents(new JoinListener(state), this);
        Bukkit.getPluginManager().registerEvents(new InteractListener(state), this);
        Bukkit.getPluginManager().registerEvents(new UIListener(state), this);
        Bukkit.getPluginManager().registerEvents(new GenListener(state), this);

        getCommand("is").setExecutor(new ChunkIsland());
        getLogger().info("ServerPlugin enabled on Minecraft 1.21");
    }
}
