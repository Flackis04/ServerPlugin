package org.bear.serverPlugin;

import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.events.DropListener;
import org.bear.serverPlugin.events.InteractListener;
import org.bear.serverPlugin.events.InventoryListener;
import org.bear.serverPlugin.events.JoinListener;
import org.bear.serverPlugin.ui.PhoneUI;
import org.bear.serverPlugin.ui.ScoreboardManager;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;

import java.util.*;

public class ServerPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        ScoreboardManager scoreboardManager = new ScoreboardManager();  // Initialize scoreboard manager (no dependencies yet)

        // Temporary null to satisfy constructor, will be replaced after PluginState is created
        PluginState pluginState = new PluginState(null, scoreboardManager); // Temporarily pass null PhoneUI

        PhoneUI phoneUI = new PhoneUI(pluginState);  // Now create PhoneUI with PluginState

        pluginState.phoneUI = phoneUI;  // Set the actual PhoneUI inside PluginState

        // Register event listeners with updated PluginState
        Bukkit.getPluginManager().registerEvents(new DropListener(pluginState, phoneUI), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(pluginState), this);
        Bukkit.getPluginManager().registerEvents(new InteractListener(pluginState), this);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(pluginState), this);

        getLogger().info("ServerPlugin enabled on Minecraft 1.21");
    }

}
