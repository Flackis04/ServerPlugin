package org.bear.serverPlugin.events;

import org.bear.serverPlugin.data.PluginState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;

public class JoinListener implements Listener {
    private final PluginState state;

    public JoinListener(PluginState pluginState) {
        this.state = pluginState;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ItemStack phone = new ItemStack(Material.DIRT);

        CustomModelData modelData = CustomModelData
                .customModelData()           // obtain a new builder :contentReference[oaicite:0]{index=0}
                .addString("phone")          // add your identifier :contentReference[oaicite:1]{index=1}
                .build();                    // finalize the component

        phone.setData(
                DataComponentTypes.CUSTOM_MODEL_DATA,  // controls the minecraft:custom_model_data NBT tag :contentReference[oaicite:2]{index=2}
                modelData
        );
        if (!player.getInventory().contains(phone))
            player.getInventory().addItem(phone);
        ItemStack gen = new ItemStack(Material.IRON_BLOCK);
        if (!player.getInventory().contains(gen))
            player.getInventory().addItem(gen);

        // (Optional) Initialize your scoreboard sidebar
        state.scoreboardManager.createSidebar(player, state.crypto);
    }

    public void onPlayerDeath

}
