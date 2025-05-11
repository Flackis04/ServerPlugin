package org.bear.serverPlugin.commands;

import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.world.GenManager;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bear.serverPlugin.utils.ItemUtils;
import org.bukkit.inventory.ItemStack;

public class Gens implements CommandExecutor {
    private final PluginState state;
    private final GenManager gen;

    public Gens(PluginState state, GenManager gen) {
        this.state = state;
        this.gen = gen;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        for (Location loc : state.getPlayerData(player.getUniqueId()).getGenLocations()) {
            gen.onGenEvent(player, loc.getBlock(), true);
        }
        return true;
    }
}
