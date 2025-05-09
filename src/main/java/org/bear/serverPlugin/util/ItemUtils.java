package org.bear.serverPlugin.util;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {
    public static ItemStack getPhone(){
        ItemStack phone = new ItemStack(Material.DIRT);

        CustomModelData phoneData = CustomModelData
                .customModelData()           // obtain a new builder :contentReference[oaicite:0]{index=0}
                .addString("phone")          // add your identifier :contentReference[oaicite:1]{index=1}
                .build();                    // finalize the component

        phone.setData(
                DataComponentTypes.CUSTOM_MODEL_DATA,  // controls the minecraft:custom_model_data NBT tag :contentReference[oaicite:2]{index=2}
                phoneData
        );

// Create and configure ItemMeta
        ItemMeta meta = phone.getItemMeta();
        if (meta != null) {
            // Set a unique display name
            meta.setDisplayName(ChatColor.AQUA + "Phone");

            // Set a custom description (lore)
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "A high-tech device");
            meta.setLore(lore);

            // Apply the ItemMeta to the ItemStack
            phone.setItemMeta(meta);
        }
        return phone;
    }

    public static ItemStack getGen(){
        ItemStack gen = new ItemStack(Material.IRON_BLOCK);

        CustomModelData genData = CustomModelData
                .customModelData()           // obtain a new builder :contentReference[oaicite:0]{index=0}
                .addString("gen")          // add your identifier :contentReference[oaicite:1]{index=1}
                .build();                    // finalize the component

        gen.setData(
                DataComponentTypes.CUSTOM_MODEL_DATA,  // controls the minecraft:custom_model_data NBT tag :contentReference[oaicite:2]{index=2}
                genData
        );

        // Create and configure ItemMeta
        ItemMeta meta = gen.getItemMeta();
        if (meta != null) {
            // Set a unique display name
            meta.setDisplayName(ChatColor.WHITE + "Generator");

            // Set a custom description (lore)
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Generates Valuables");
            meta.setLore(lore);

            // Apply the ItemMeta to the ItemStack
            gen.setItemMeta(meta);
        }

        return gen;
    }

    public static boolean isGen(Block block) {
        // You can store UUID in block data or just check location if needed
        return block.getType() == Material.IRON_BLOCK;
    }

    public static boolean isPhone(ItemStack item) {
        if (item == null || item.getType() != Material.DIRT || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().equals("§6Phone");
    }
}
