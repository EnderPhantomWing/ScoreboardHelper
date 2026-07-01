package top.pigest.scoreboardhelper;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.scores.*;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.Scoreboard;
import org.slf4j.Logger;
import top.pigest.scoreboardhelper.command.SBHelperCommand;
import top.pigest.scoreboardhelper.config.ScoreboardHelperConfig;
import top.pigest.scoreboardhelper.gui.screen.ScoreEditingScreen;
import top.pigest.scoreboardhelper.gui.screen.ScoreboardHelperConfigScreen;
import top.pigest.scoreboardhelper.util.Constants;
import top.pigest.scoreboardhelper.gui.screen.ScoreboardExportScreen;
import top.pigest.scoreboardhelper.util.KeyBindings;
import top.pigest.scoreboardhelper.util.ScoreboardHelperUtils;

import java.util.Collection;

public class ScoreboardHelper implements ClientModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
        KeyBindings.init();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> SBHelperCommand.register(dispatcher));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Constants.CD_SWITCH_DISPLAY = Constants.CD_SWITCH_DISPLAY > 0 ? Constants.CD_SWITCH_DISPLAY - 1: 0;
            Constants.CD_EXPORT = Constants.CD_EXPORT > 0 ? Constants.CD_EXPORT - 1: 0;
            Constants.CD_EDIT = Constants.CD_EDIT > 0 ? Constants.CD_EDIT - 1: 0;
            if (KeyBindings.KEY_BINDING_PAGE_DOWN.isDown()) {
                Scoreboard scoreboard;
                if (client.level != null && client.player != null) {
                    scoreboard = client.level.getScoreboard();
                    Objective scoreboardObjective = ScoreboardHelperUtils.getSidebarObjective(scoreboard, client.player);
                    Collection<PlayerScoreEntry> collection = scoreboard.listPlayerScores(scoreboardObjective);
                    if(collection.size() > Constants.PAGE + ScoreboardHelperConfig.INSTANCE.maxShowCount.getValue()) {
                        Constants.PAGE++;
                    }
                }
            }
            if (KeyBindings.KEY_BINDING_PAGE_UP.isDown()) {
                Constants.PAGE = Constants.PAGE > 0 ? Constants.PAGE - 1 : 0;
            }
            if (KeyBindings.KEY_BINDING_SWITCH_DISPLAY.isDown() && Constants.CD_SWITCH_DISPLAY == 0) {
                ScoreboardHelperConfig.INSTANCE.scoreboardShown.setValue(!ScoreboardHelperConfig.INSTANCE.scoreboardShown.getValue());
                Constants.CD_SWITCH_DISPLAY = 5;
            }
            if (KeyBindings.KEY_BINDING_OPEN_CONFIG.isDown()) {
                client.setScreen(new ScoreboardHelperConfigScreen(client.screen, ScoreboardHelperConfig.INSTANCE));
            }
            if(KeyBindings.KEY_BINDING_EXPORT_SCOREBOARD.isDown() && Constants.CD_EXPORT == 0) {
                ScoreboardExportScreen.INSTANCE.setParent(client.screen);
                client.setScreen(ScoreboardExportScreen.INSTANCE);
                Constants.CD_EXPORT = 5;
            }
            if (KeyBindings.KEY_BINDING_EDIT_SCORE.isDown() && Constants.CD_EDIT == 0) {
                if (client.level != null && client.player != null) {
                    Scoreboard scoreboard = client.level.getScoreboard();
                    Objective objective = ScoreboardHelperUtils.getSidebarObjective(scoreboard, client.player);
                    if(objective == null) {
                        client.player.sendSystemMessage(Component.translatable("hint.scoreboard-helper.export.fail.inactive").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                    } else {
                        client.setScreen(new ScoreEditingScreen(client.screen, scoreboard, objective));
                    }
                }
                Constants.CD_EDIT = 5;
            }
        });
    }
}
