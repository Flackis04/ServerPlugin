package org.bear.serverPlugin;

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

    private static final String CRYPTO_LABEL = "§aCrypto: §f";

    public void createSidebar(Player player, int crypto) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("info", "dummy", "§9Your Stats");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Use constant label for consistency
        objective.getScore(CRYPTO_LABEL + crypto).setScore(0);

        player.setScoreboard(scoreboard);

        UUID uuid = player.getUniqueId();
        playerScoreboards.put(uuid, scoreboard);
        playerObjectives.put(uuid, objective);
        playerCrypto.put(uuid, crypto);
    }

    public void updateCrypto(Player player, int newCrypto) {
        UUID uuid = player.getUniqueId();
        Objective objective = playerObjectives.get(uuid);
        Scoreboard scoreboard = playerScoreboards.get(uuid);
        Integer oldCrypto = playerCrypto.get(uuid);

        if (objective == null || scoreboard == null || oldCrypto == null) return;

        // Remove the old score
        scoreboard.resetScores(CRYPTO_LABEL + oldCrypto);

        // Add the updated score
        objective.getScore(CRYPTO_LABEL + newCrypto).setScore(0);

        // Save the new value
        playerCrypto.put(uuid, newCrypto);
    }
}
