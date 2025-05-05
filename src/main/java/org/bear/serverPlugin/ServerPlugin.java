package org.bear.serverPlugin;

import org.bear.serverPlugin.UI.ScoreboardManager;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class ServerPlugin extends JavaPlugin implements Listener {

    // Registering events and commands

    List<Material> orderedMats = List.of(
            Material.DIRT,
            Material.STONE,
            Material.COAL,
            Material.IRON_INGOT,
            Material.GOLD_INGOT,
            Material.DIAMOND,
            Material.EMERALD,
            Material.NETHERITE_SCRAP,
            Material.NETHERITE_INGOT
    );

    Map<Material, Double> valuables = generateExponentialWeights(orderedMats, 0.27);



    //Wood








    int crypto = 10000;
    int delayLevel = 1;
    private int delayTicks = 20;

    ScoreboardManager scoreboardManager = new ScoreboardManager();

    public Map<Material, Double> generateExponentialWeights(List<Material> materials, double ratio) {
        int count = materials.size();
        double[] rawWeights = new double[count];
        double sum = 0.0;

        // Step 1: Compute exponential weights
        for (int i = 0; i < count; i++) {
            rawWeights[i] = Math.pow(ratio, i);
            sum += rawWeights[i];
        }

        // Step 2: Normalize
        for (int i = 0; i < count; i++) {
            rawWeights[i] /= sum;
        }

        // Step 3: Assign to materials
        Map<Material, Double> valueMap = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            valueMap.put(materials.get(i), rawWeights[i]);
        }

        return valueMap;
    }

    @Override
    public void onEnable() {

        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("ServerPlugin enabled on Minecraft 1.21");
    }

    @Override
    public void onDisable() {
        getLogger().info("ServerPlugin disabled");
    }

    // Welcome message for first-time players
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        scoreboardManager.createSidebar(player, crypto);

        if (!player.hasPlayedBefore()) {
            player.sendMessage("§aWelcome!");

            // Pretend this works just like the /give command with custom NBT
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "give " + player.getName() + " dirt[minecraft:custom_model_data={strings:['phone']}]");
        }
    }


    // Handle right-click interactions with custom model data items
    private final Set<UUID> cooldownPlayers = new HashSet<>();

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != RIGHT_CLICK_AIR && event.getAction() != RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Check if player is in cooldown
        if (cooldownPlayers.contains(uuid)) {
            player.sendMessage("§cPlease wait before using that again!");
            return;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = itemInHand.getItemMeta();

        if (itemMeta != null && itemMeta.hasCustomModelData()) {
            player.sendMessage("§aYou right-clicked with a custom model item!");
            player.getInventory().addItem(new ItemStack(getRandomMaterialFromMap(valuables)));

            // Add player to cooldown list
            cooldownPlayers.add(uuid);

            delayTicks = Math.max(20 - (delayLevel - 1), 5);

            Bukkit.getScheduler().runTaskLater(this, () -> cooldownPlayers.remove(uuid), delayTicks);

        }
    }


    @EventHandler
    public void onDropKey(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        ItemMeta itemMeta = droppedItem.getItemMeta();

        if (itemMeta != null && itemMeta.hasCustomModelData()) {
            event.setCancelled(true); // Prevent dropping the item (optional)
            player.sendMessage("§aYou opened a UI!");

            // Create the inventory UI
            Inventory inv = Bukkit.createInventory(null, 54, Component.text("Phone"));

            // Create the CPU item stack (Emerald Block)
            ItemStack temp = new ItemStack(Material.EMERALD_BLOCK);
            ItemMeta meta = temp.getItemMeta();

            if (meta != null) {
                // Set custom display name
                meta.setDisplayName(ChatColor.GRAY + "CPU");

                // Set custom lore
                meta.setLore(Arrays.asList(
                        ChatColor.DARK_GRAY + "Regulates your phone's clock speed",
                        ChatColor.GRAY + "Current Level : " + delayLevel + " : " + delayTicks/20 + "s"
                ));

                // Hide default item attributes (like "Unbreakable" or damage)
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

                // Set the meta back to the item
                temp.setItemMeta(meta);
            }

            // Set the CPU item in the inventory
            inv.setItem(22, temp);

            // Open the inventory for the player
            player.openInventory(inv);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if the inventory title is your custom one
        if (event.getView().title().toString().contains("Phone")) {
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            if (clickedItem.getType() == Material.EMERALD_BLOCK) {
                event.setCancelled(true);  // Prevent taking the item

                int price = 500;
                if (crypto >= price) {
                    // Deduct the price and upgrade the CPU level
                    crypto = crypto - price;
                    delayLevel = delayLevel + 1;

                    // Send feedback to the player about the upgrade
                    player.sendMessage("New CPU chip installed: Intel Core i" + delayLevel);

                    // Update the player's crypto on the scoreboard
                    scoreboardManager.updateCrypto(player, crypto);

                    // After upgrade, refresh the inventory UI with updated CPU level
                    Inventory inv = Bukkit.createInventory(null, 54, Component.text("Phone"));

                    // Create the updated CPU item stack
                    ItemStack updatedCpu = new ItemStack(Material.EMERALD_BLOCK);
                    ItemMeta meta = updatedCpu.getItemMeta();

                    if (meta != null) {
                        // Set custom display name
                        meta.setDisplayName(ChatColor.GRAY + "CPU");

                        // Set custom lore with the updated CPU level
                        meta.setLore(Arrays.asList(
                                ChatColor.DARK_GRAY + "Regulates your phone's clock speed",
                                ChatColor.GRAY + "Current Level : " + delayLevel
                        ));

                        // Hide default item attributes
                        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

                        // Set the meta back to the updated item
                        updatedCpu.setItemMeta(meta);
                    }

                    // Set the updated CPU item in the inventory
                    inv.setItem(22, updatedCpu);

                    // Open the updated inventory for the player
                    player.openInventory(inv);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                } else {
                    player.sendMessage(ChatColor.RED + "Not enough crypto!");
                }
            }
        }
    }




    private Material getRandomMaterialFromMap(Map<Material, Double> weightedMap) {
        double random = Math.random();
        double cumulative = 0.0;

        for (Map.Entry<Material, Double> entry : weightedMap.entrySet()) {
            cumulative += entry.getValue();
            if (random <= cumulative) {
                return entry.getKey();
            }
        }

        // Fallback (in case of rounding error)
        return Material.DIRT;
    }



}
