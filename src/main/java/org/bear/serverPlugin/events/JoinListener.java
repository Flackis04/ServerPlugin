package org.bear.serverPlugin.events;

import org.bear.serverPlugin.data.PluginState;
import org.bukkit.Color;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class JoinListener implements Listener {

    private final PluginState state; // Declare a field to store the plugin state

    // Constructor to initialize the PluginState
    public JoinListener(PluginState pluginState) {
        this.state = pluginState; // Assign the provided PluginState to the class field
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("hi");
        state.scoreboardManager.createSidebar(player, state.crypto);

        CustomModelDataComponent yes = new CustomModelDataComponent() {
            @Override
            public @NotNull List<Float> getFloats() {
                return List.of();
            }

            @Override
            public void setFloats(@NotNull List<Float> list) {

            }

            @Override
            public @NotNull List<Boolean> getFlags() {
                return List.of();
            }

            @Override
            public void setFlags(@NotNull List<Boolean> list) {

            }

            @Override
            public @NotNull List<String> getStrings() {
                return List.of();
            }

            @Override
            public void setStrings(@NotNull List<String> list) {

            }

            @Override
            public @NotNull List<Color> getColors() {
                return List.of();
            }

            @Override
            public void setColors(@NotNull List<Color> list) {

            }

            @Override
            public @NotNull Map<String, Object> serialize() {
                return Map.of();
            }
        };
        List<String> phoneList = Arrays.asList("phone");
        yes.setStrings(phoneList);

        // ✅ Give the player a dirt block every time they join
        ItemStack phone = new ItemStack(Material.DIRT);
        ItemMeta meta = phone.getItemMeta();
        if (meta != null) {
            // Set the custom model data (e.g., 123)
            meta.setCustomModelDataComponent(yes);

            // Set the item meta back to the item
            phone.setItemMeta(meta);
        }

        //temp
        ItemStack temp = new ItemStack(Material.DIRT);

        player.getInventory().addItem(temp);
    }
}
