package org.bear.serverPlugin.utils;

import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.world.GenManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public class BlockUtils {

    private final PluginState state;

    public BlockUtils(PluginState state) {
        this.state = state;
    }

    public static boolean isGenLocation(Block block, PluginState state, Player player) {
        return state.getPlayerData(player.getUniqueId()).getGenLocations().contains(block.getLocation());
    }
}