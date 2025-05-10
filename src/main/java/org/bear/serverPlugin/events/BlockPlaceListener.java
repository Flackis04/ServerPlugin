package org.bear.serverPlugin.events;

import org.bear.serverPlugin.data.PlayerData;
import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.util.ItemUtils;
import org.bear.serverPlugin.world.GenManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class BlockPlaceListener implements Listener {
    private final PluginState state;
    private final GenManager gen;

    public BlockPlaceListener(PluginState state, GenManager gen) {
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

            if (playerData.gensPlaced < playerData.slotLevel) {
                Location loc = placedBlock.getLocation();

                // Add the new location to the player's gen locations set
                Set<Location> genLocations = playerData.getGenLocations();
                genLocations.add(loc);
                playerData.setGenLocations(genLocations);

                // Place the generator
                gen.placeGenForPlayer(player, loc);

                // Update gensPlaced
                playerData.gensPlaced += 1;

                player.sendMessage("§aPlaced gens: " + playerData.gensPlaced + "/" + playerData.slotLevel);
            } else {
                Bukkit.broadcastMessage("Someone is duping");
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block brokenBlock = event.getBlock();
        Player player = event.getPlayer();
        Location loc = brokenBlock.getLocation();
        if (event.getBlock().equals(ItemUtils.getGen())) {
            PlayerData playerData = state.getPlayerData(player.getUniqueId());
            Set<Location> genLocations = playerData.getGenLocations();

            // Remove if the broken block's location matches any generator location
            boolean removed = genLocations.removeIf(existingLoc ->
                    existingLoc.getWorld().equals(loc.getWorld()) &&
                            existingLoc.getBlockX() == loc.getBlockX() &&
                            existingLoc.getBlockY() == loc.getBlockY() &&
                            existingLoc.getBlockZ() == loc.getBlockZ()
            );

            if (removed) {
                playerData.setGenLocations(genLocations);
                playerData.gensPlaced -= 1;
                player.sendMessage("§cRemoved gen. " + playerData.gensPlaced + "/" + playerData.slotLevel + "gens placed");
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
            if (!data.getMatInCollection().contains(mat)) {
                data.getMatInCollection().add(mat);
                state.collectionUI.createCollectionMat(mat);
                //state.collectionUI.updateCollectionUI(player);
            }
        }

    }
}
