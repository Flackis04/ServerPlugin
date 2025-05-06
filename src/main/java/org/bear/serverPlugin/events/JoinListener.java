package org.bear.serverPlugin.events;

import org.bear.serverPlugin.data.PluginState;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;

public class JoinListener implements Listener {

    private final PluginState state;

    public JoinListener(PluginState state) {
        this.state = state;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("Welcome to the server! You have " + state.crypto + " crypto.");

        // ✅ Create the scoreboard on join
        state.scoreboardManager.createSidebar(player, state.crypto);
    }

}
