package org.bear.serverPlugin.UI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {

    private final Map<UUID, Scoreboard> playerScoreboards = new HashMap<>();
    private final Map<UUID, Objective> playerObjectives = new HashMap<>();
    private final Map<UUID, Integer> playerCrypto = new HashMap<>();

    public void createSidebar(Player player, int crypto) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("info", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        objective.getScore("§aCrypto: §f" + crypto).setScore(0);

        player.setScoreboard(scoreboard);

        playerScoreboards.put(player.getUniqueId(), scoreboard);
        playerObjectives.put(player.getUniqueId(), objective);
        playerCrypto.put(player.getUniqueId(), crypto);
    }

    public void updateCrypto(Player player, int newCrypto) {
        UUID uuid = player.getUniqueId();
        Objective objective = playerObjectives.get(uuid);
        Scoreboard scoreboard = playerScoreboards.get(uuid);
        Integer oldCrypto = playerCrypto.get(uuid);

        if (objective == null || scoreboard == null || oldCrypto == null) return;

        // Remove old crypto line
        scoreboard.resetScores("§eCrypto: §a" + oldCrypto);

        // Add new line
        objective.getScore("§eCrypto: §a" + newCrypto).setScore(3);

        // Update stored value
        playerCrypto.put(uuid, newCrypto);
    }
}
