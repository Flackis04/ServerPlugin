package org.bear.serverPlugin.events;

import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class JoinListener implements Listener {
    private final PluginState state;

    public JoinListener(PluginState pluginState) {
        this.state = pluginState;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.getInventory().contains(ItemUtils.getPhone()))
            player.getInventory().addItem(ItemUtils.getPhone());



        if (!player.getInventory().contains(ItemUtils.getGen()))
            player.getInventory().addItem(ItemUtils.getGen());

        // (Optional) Initialize your scoreboard sidebar
        state.scoreboardManager.createSidebar(player, state.getPlayerData(player.getUniqueId()).crypto);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        player.getInventory().addItem(ItemUtils.getPhone());
        player.getInventory().addItem(ItemUtils.getGen());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
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
