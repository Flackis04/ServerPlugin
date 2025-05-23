package org.bear.serverPlugin.commands;

import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.world.GeneratorTask;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class Gens implements CommandExecutor {
    private final PluginState state;
    private final GeneratorTask gen;

    public Gens(PluginState state, GeneratorTask gen) {
        this.state = state;
        this.gen = gen;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        for (Location loc : state.getPlayerData(player.getUniqueId()).getGenerators()) {
            gen.onGenRemoveEvent(player, loc.getBlock(), true);
        }
        return true;
    }
}
