package org.bear.serverPlugin.events;

import org.apache.commons.lang3.ObjectUtils;
import org.bear.serverPlugin.data.PlayerData;
import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.utils.ItemUtils;
import org.bear.serverPlugin.world.GenManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class BlockListener implements Listener {
    private final PluginState state;
    private final GenManager gen;

    public BlockListener(PluginState state, GenManager gen) {
        this.state = state;
        this.gen = gen;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlock();
        Player player = event.getPlayer();

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.equals(ItemUtils.getGen())) {
            PlayerData playerData = state.getPlayerData(player.getUniqueId());

            if (playerData.generators.stream().filter(gen -> gen.location != null).count() < playerData.maxGenerators) {
                Location loc = placedBlock.getLocation();

                // Add the new location to the player's gen locations set
                //genLocations.add(loc);
                //playerData.generators

                // Place the generator
                gen.placeGenForPlayer(player, loc);

                //player.sendMessage("§aPlaced gens: " + playerData.gensPlaced + "/" + playerData.slotLevel);
            } else {
                Bukkit.broadcastMessage("Someone is duping");
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block destroyedBlock = event.getBlock();
        Player player = event.getPlayer();
        gen.onGenRemoveEvent(player, destroyedBlock, false);
    }


    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack item = event.getItem().getItemStack();
        Material mat = item.getType();
        state.getPlayerData(player.getUniqueId()).seenMaterials.add(mat);

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

        if (hasSellPrice && state.orderedMats.contains(mat) && !data.seenMaterials.contains(mat)) {
            data.seenMaterials.add(mat);
            state.collectionUI.createCollectionMat(mat);
            //state.collectionUI.updateCollectionUI(player);
        }
    }
}

