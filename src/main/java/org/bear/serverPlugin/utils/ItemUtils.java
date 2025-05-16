package org.bear.serverPlugin.utils;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bear.serverPlugin.data.PluginState;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

    private final PluginState state;
    private final JavaPlugin plugin;
    public ItemUtils(PluginState state, JavaPlugin plugin) {
        this.state = state;
        this.plugin = plugin;
    }

    public static ItemStack getPhone(){
        ItemStack phone = new ItemStack(Material.CRYING_OBSIDIAN);

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

    public static boolean isGen(ItemStack item) {
        // Check if the item is null
        if (item == null) {
            return false;
        }

        // Check if the item type matches the phone's type
        if (!item.getType().equals(getGen().getType())) {
            return false; // If the item type is not the same, it's not the phone
        }

        // Check if the item has the same metadata (custom name, lore, etc.)
        return item.isSimilar(getGen()); // .isSimilar() compares the type and all relevant metadata
    }

    public static boolean isPhone(ItemStack item) {
        // Check if the item is null
        if (item == null) {
            return false;
        }

        // Check if the item type matches the phone's type
        if (!item.getType().equals(getPhone().getType())) {
            return false; // If the item type is not the same, it's not the phone
        }

        // Check if the item has the same metadata (custom name, lore, etc.)
        return item.isSimilar(getPhone()); // .isSimilar() compares the type and all relevant metadata
    }

    public static void ensureHasItems(Player player, PluginState state){
        if (!player.getInventory().contains(ItemUtils.getPhone())) {
            player.getInventory().addItem(ItemUtils.getPhone());
        }
        if (!player.getInventory().contains(ItemUtils.getGen()) && state.getPlayerData(player.getUniqueId()).generators.stream().noneMatch(gen -> gen.location !=null)) {
            player.getInventory().addItem(ItemUtils.getGen());
        }
    }

    public static ItemStack Button(Material mat, String name, String description) {
        ItemStack button = new ItemStack(mat);
        ItemMeta meta = button.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + name);
            meta.setLore(List.of(
                    ChatColor.DARK_GRAY + description
            ));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            button.setItemMeta(meta);
        }

        return button;
    }
}
