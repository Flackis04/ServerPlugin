package org.bear.serverPlugin.events;

import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.utils.BlockUtils;
import org.bear.serverPlugin.utils.ItemUtils;
import org.bear.serverPlugin.world.GeneratorTask;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class InteractListener implements Listener {

    private final PluginState state;
    private final GeneratorTask gen;

    public InteractListener(PluginState state, GeneratorTask gen) {
        this.state = state;
        this.gen = gen;
    }
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!Objects.equals(event.getHand(), EquipmentSlot.HAND)) return;
        if (event.getAction().isRightClick()) {
            Player player = event.getPlayer();
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (ItemUtils.isPhone(itemInHand)) {
                event.setCancelled(true);
                state.phoneUI.openPhoneUI(player);
                return;
            }

            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) return;
            if (player.isSneaking()) return;

            if (BlockUtils.isGenLocation(clickedBlock, state, player)) {
                event.setCancelled(true);
                state.genUI.openGenUI(player);
            }
        }
    }
}

