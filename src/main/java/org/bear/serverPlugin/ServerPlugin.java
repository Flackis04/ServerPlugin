package org.bear.serverPlugin;

import org.bear.serverPlugin.data.Database;
import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.events.*;
import org.bear.serverPlugin.commands.ChunkIsland;
import org.bear.serverPlugin.commands.Gens;
import org.bear.serverPlugin.world.DecayConcept;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ServerPlugin extends JavaPlugin {
    private static ServerPlugin plugin;
    private Database database;

    public static ServerPlugin getPlugin(){
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        ScoreboardManager scoreboardManager = new ScoreboardManager();

        database = new Database();
        database.connect();
        database.createTables();

        PluginState state = new PluginState(scoreboardManager, database);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(state, database), this);
        Bukkit.getPluginManager().registerEvents(new InteractListener(state, gen), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(state, gen), this);
        Bukkit.getPluginManager().registerEvents(new DecayConcept(state), this);

        Objects.requireNonNull(getCommand("is")).setExecutor(new ChunkIsland());
        Objects.requireNonNull(getCommand("getgens")).setExecutor(new Gens(state, gen));

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
