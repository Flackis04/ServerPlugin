package org.bear.serverPlugin.events;

import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.util.ItemUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.Objects;

public class InteractListener implements Listener {

    private final PluginState state;

    public InteractListener(PluginState state) {
        this.state = state;
    }
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        // Only handle right-click with MAIN_HAND to avoid duplicate trigger from OFF_HAND
        if (!Objects.equals(event.getHand(), EquipmentSlot.HAND)) return;

        if (event.getAction().isRightClick()) {
            Player player = event.getPlayer();
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            // Handle phone right-click
            if (itemInHand.equals(ItemUtils.getPhone())) {
                event.setCancelled(true);
                state.phoneUI.openPhoneUI(player);
                return;
            }

            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) return;

            Location playerGenLoc = state.getPlayerData(player.getUniqueId()).getGenLocation();
            if (playerGenLoc == null) return;

            if (ItemUtils.isGen(clickedBlock) && clickedBlock.getLocation().equals(playerGenLoc)) {
                clickedBlock.setType(Material.AIR);
                player.getInventory().addItem(ItemUtils.getGen());
            }
        }
    }
}
