package org.bear.serverPlugin.events;

import org.bear.serverPlugin.data.Database;
import org.bear.serverPlugin.data.PlayerData;
import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.utils.ItemUtils;
import org.bear.serverPlugin.world.GeneratorTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
    private final PluginState state;
    private final Database database;

    public PlayerListener(PluginState pluginState, Database database) {
        this.state = pluginState;
        this.database = database;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (state.getPlayerData(player.getUniqueId()).generators.stream().anyMatch(gen -> gen.location != null)){
            GeneratorTask.startGenLoop(player);
            player.sendMessage(String.valueOf(state.getPlayerData(player.getUniqueId()).generators.stream().filter(gen -> gen.location != null).count()));
        }

        // Create a scoreboard for the player
        state.scoreboardManager.createSidebar(player, state.getPlayerData(player.getUniqueId()).crypto);

        // Give the player their items if they don't have them
        ItemUtils.ensureHasItems(player,state);

        int playerId = player.getUniqueId().hashCode();



        // Load player data from the database
        PlayerData playerData = database.loadPlayerData(playerId);

        if (playerData == null) {
            // Create default player data if none exists
            playerData = new PlayerData();
            database.updatePlayerData(playerId, playerData);  // Save default data if no data exists for the player
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        int playerId = player.getUniqueId().hashCode();

        PlayerData playerData = state.getPlayerData(player.getUniqueId());

        // Save the player's data to the database using the updatePlayerData method
        database.updatePlayerData(playerId, playerData);

        // Optionally, disconnect from the database if needed (optional)
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        ItemUtils.ensureHasItems(event.getPlayer(),state);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        event.getDrops().remove(ItemUtils.getPhone());
        event.getDrops().remove(ItemUtils.getGen());
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack dropped = event.getItemDrop().getItemStack();

        if (dropped.equals(ItemUtils.getPhone())) {
            event.setCancelled(true);
        }

        if (dropped.equals(ItemUtils.getGen())) {
            event.setCancelled(true);
        }
    }
}
