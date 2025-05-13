package org.bear.serverPlugin.utils;

import org.bear.serverPlugin.data.PluginState;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GenUtils {
    public static ItemStack randomValuable(PluginState state) {

        ItemStack item = new ItemStack(MaterialUtils.getRandomMaterialFromMap(state.valuables));
        var meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            String description = "Sell price: " + state.sellPrices.getOrDefault(item.getType(), 0);
            lore.add(description);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}