package org.bear.serverPlugin.events;

import org.bear.serverPlugin.data.PlayerData;
import org.bear.serverPlugin.data.PluginState;
import org.bear.serverPlugin.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bear.serverPlugin.util.MaterialUtils;

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

        // Get the item the player is holding
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        // Check if the meta contains custom model data or a tag (replace 123456 with your custom model data)
        if (itemInHand.equals(ItemUtils.getGen())) { //isgen, not equals
            Location loc = placedBlock.getLocation(); // or clicked block, etc.
            state.placeGenForPlayer(player, loc);
        }
    }


    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack item = event.getItem().getItemStack();
        Material mat = item.getType();
        state.seenMaterials.put(mat, true);  // mark it as seen

// Check if the item has lore and contains "Sell price: "
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
