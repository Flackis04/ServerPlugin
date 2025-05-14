package org.bear.serverPlugin.utils;

import org.bear.serverPlugin.data.PluginState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GenUtils {
    public static ItemStack randomValuable(PluginState state, Player player) {

        ItemStack item = new ItemStack(MaterialUtils.getRandomMaterialFromMap(state.getPlayerData(player.getUniqueId()).valuables));
        var meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            Material type = item.getType();
            int sellPrice = state.getPlayerData(player.getUniqueId()).sellPrices.getOrDefault(type, 0);

            String description = "Sell price: " + sellPrice + "\n" +
                    "Drop chance: " + String.format("%.2f", "dropchance") + "%";
            lore.add(description);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}