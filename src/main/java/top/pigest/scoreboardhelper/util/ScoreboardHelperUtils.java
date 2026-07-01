package top.pigest.scoreboardhelper.util;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;

public class ScoreboardHelperUtils {
    public static Objective getSidebarObjective(Scoreboard scoreboard, LocalPlayer player) {
        PlayerTeam team = scoreboard.getPlayersTeam(player.getScoreboardName());
        Objective objective = null;
        if (team != null && DisplaySlot.teamColorToSlot(team.getColor()) != null) {
            objective = scoreboard.getDisplayObjective(DisplaySlot.teamColorToSlot(team.getColor()));
        }
        objective = objective != null ? objective : scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
        return objective;
    }

}
