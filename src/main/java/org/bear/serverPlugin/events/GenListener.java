package org.bear.serverPlugin.events;

import org.bear.serverPlugin.data.PlayerData;
import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class GenListener implements Listener {
    private final PluginState state;

    public GenListener(PluginState state) {
        this.state = state;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlock();
        Player player = event.getPlayer();

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.equals(ItemUtils.getGen())) {
            if (state.getPlayerData(player.getUniqueId()).gensPlaced < state.getPlayerData(player.getUniqueId()).slotLevel){
                Location loc = placedBlock.getLocation();
                state.placeGenForPlayer(player, loc);
                state.getPlayerData(player.getUniqueId()).gensPlaced += 1;
                player.sendMessage("§aPlaced gens: " + state.getPlayerData(player.getUniqueId()).gensPlaced + "/" + state.getPlayerData(player.getUniqueId()).slotLevel);
            }
            else {
                Bukkit.broadcastMessage("Someone is duping");
            }
        }
    }


    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack item = event.getItem().getItemStack();
        Material mat = item.getType();
        state.seenMaterials.put(mat, true);

        boolean hasSellPrice = false;
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasLore()) {
            for (String line : Objects.requireNonNull(meta.getLore())) {
                if (line.contains("Sell price: ")) {
                    hasSellPrice = true;
                    break;
                }
            }
        }

        PlayerData data = state.getPlayerData(player.getUniqueId());

        if (hasSellPrice && state.orderedMats.contains(mat) && state.seenMaterials.getOrDefault(mat, false)) {
            if (!data.matInCollection.contains(mat)) {
                data.matInCollection.add(mat);
                state.collectionUI.createCollectionMat(mat);
                //state.collectionUI.updateCollectionUI(player);
            }
        }

    }
}
