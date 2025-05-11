package org.bear.serverPlugin.events;

import org.bear.serverPlugin.data.Database;
import org.bear.serverPlugin.data.PlayerData;
import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.utils.InventoryUtils;
import org.bear.serverPlugin.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class JoinListener implements Listener {
    private final PluginState state;
    private final Database database;

    public JoinListener(PluginState pluginState, Database database) {
        this.state = pluginState;
        this.database = database;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        state.getPlayerData(player.getUniqueId()).genIsActive = true;

        // Create a scoreboard for the player
        state.scoreboardManager.createSidebar(player, state.getPlayerData(player.getUniqueId()).crypto);

        // Give the player their items if they don't have them
        if (!player.getInventory().contains(ItemUtils.getPhone())) {
            player.getInventory().addItem(ItemUtils.getPhone());
        }
        if (!player.getInventory().contains(ItemUtils.getGen()) && state.getPlayerData(player.getUniqueId()).gensPlaced == 0) {
            player.getInventory().addItem(ItemUtils.getGen());
        }
        int playerId = player.getUniqueId().hashCode();



        // Load player data from the database
        PlayerData playerData = database.loadPlayerData(playerId);

        if (playerData == null) {
            // Create default player data if none exists
            playerData = new PlayerData();
            database.updatePlayerData(playerId, playerData);  // Save default data if no data exists for the player
        }

        // Now, playerData should never be null here
        String serializedInventory = InventoryUtils.serializeInventory(playerData.getInventoryItems());
        player.sendMessage("Your serialized inventory: " + serializedInventory);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        int playerId = player.getUniqueId().hashCode();

        // Retrieve the player's data from PluginState
        PlayerData playerData = state.getPlayerData(player.getUniqueId());

        // Save the player's data to the database using the updatePlayerData method
        database.updatePlayerData(playerId, playerData);

        // Optionally, disconnect from the database if needed (optional)
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        // Ensure the player gets the phone and gen on respawn
        player.getInventory().addItem(ItemUtils.getPhone());
        if (state.getPlayerData(player.getUniqueId()).gensPlaced == 0) {
            player.getInventory().addItem(ItemUtils.getGen());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Remove the phone and gen from the player's drops
        event.getDrops().remove(ItemUtils.getPhone());
        event.getDrops().remove(ItemUtils.getGen());
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack dropped = event.getItemDrop().getItemStack();

        // Prevent players from dropping the phone and gen items
        if (dropped.equals(ItemUtils.getPhone())) {
            event.setCancelled(true);
        }

        if (dropped.equals(ItemUtils.getGen())) {
            event.setCancelled(true);
        }
    }
}
