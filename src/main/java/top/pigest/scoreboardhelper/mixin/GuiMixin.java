/*
 * The MIT License
 *
 * Copyright (c) 2024 TmallKing1
 * Copyright (c) 2026 EnderPhantomWing
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package top.pigest.scoreboardhelper.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import top.pigest.scoreboardhelper.config.ScoreboardHelperConfig;
import top.pigest.scoreboardhelper.util.Constants;
import top.pigest.scoreboardhelper.util.SidebarEntry;

import java.util.Collection;
import java.util.Comparator;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow public abstract Font getFont();

    @Shadow @Final private Minecraft minecraft;

    /**
     * @author xiaozhu_zhizui
     * @reason 重写侧边栏渲染方法，以适用配置
     */
    @Overwrite
    private void displayScoreboardSidebar(GuiGraphics context, Objective objective) {
        ScoreboardHelperConfig config = ScoreboardHelperConfig.INSTANCE;
        if (!config.scoreboardShown.getValue()) {
            return;
        }
        int i;
        Scoreboard scoreboard = objective.getScoreboard();
        NumberFormat numberFormat = objective.numberFormatOrDefault(StyledFormat.SIDEBAR_DEFAULT);
        Comparator<PlayerScoreEntry> comparatorString = Comparator.comparing(entry -> entry.ownerName().tryCollapseToString(), String.CASE_INSENSITIVE_ORDER);
        Comparator<PlayerScoreEntry> comparator = switch (config.sortingMethod.getValue()) {
            case BY_SCORE_DESC -> Comparator.comparing(PlayerScoreEntry::value).reversed().thenComparing(PlayerScoreEntry::owner, String.CASE_INSENSITIVE_ORDER);
            case BY_SCORE_ASC -> Comparator.comparing(PlayerScoreEntry::value).thenComparing(PlayerScoreEntry::owner, String.CASE_INSENSITIVE_ORDER);
            case BY_NAME_DESC -> comparatorString.reversed().thenComparing(PlayerScoreEntry::value);
            case BY_NAME_ASC -> comparatorString.thenComparing(PlayerScoreEntry::value);
        };
        Collection<PlayerScoreEntry> entries = scoreboard.listPlayerScores(objective);
        SidebarEntry[] sidebarEntries = entries.stream().filter(score -> !score.isHidden()).sorted(comparator).skip(Constants.PAGE + config.maxShowCount.getValue() > entries.size() ? Math.max(entries.size() - config.maxShowCount.getValue(), 0) : Constants.PAGE).limit(config.maxShowCount.getValue()).map(scoreboardEntry -> {
            PlayerTeam team = scoreboard.getPlayersTeam(scoreboardEntry.owner());
            Component text = scoreboardEntry.ownerName();
            MutableComponent text2 = PlayerTeam.formatNameForTeam(team, text);
            MutableComponent text3 = scoreboardEntry.formatValue(numberFormat);
            int width = this.getFont().width(text3);
            return new SidebarEntry(text2, text3, width);
        }).toArray(SidebarEntry[]::new);
        Component text = objective.getDisplayName();
        int j = i = this.getFont().width(text);
        int joinerWidth = this.getFont().width(": ");
        for (SidebarEntry sidebarEntry : sidebarEntries) {
            j = Math.max(j, this.getFont().width(sidebarEntry.name()) + (sidebarEntry.scoreWidth() > 0 && config.sidebarScoreShown.getValue() ? joinerWidth + sidebarEntry.scoreWidth() : 0));
        }
        int finalJ = j;
        context.drawManaged(() -> {
            int length = sidebarEntries.length;
            int m = switch(config.sidebarPosition.getValue()) {
                case LEFT, RIGHT -> context.guiHeight() / 2 + length * 3;
                case LEFT_UPPER_CORNER, RIGHT_UPPER_CORNER -> (length + 1) * this.getFont().lineHeight + 2;
                case LEFT_LOWER_CORNER, RIGHT_LOWER_CORNER -> context.guiHeight() - 2;
            };
            m += config.sidebarYOffset.getValue();
            int o = switch (config.sidebarPosition.getValue()) {
                case LEFT, LEFT_LOWER_CORNER, LEFT_UPPER_CORNER -> 5;
                case RIGHT, RIGHT_LOWER_CORNER, RIGHT_UPPER_CORNER -> context.guiWidth() - finalJ - 3;
            };
            int p = o + finalJ + 2;
            int backgroundOpacity = this.minecraft.options.getBackgroundColor(config.sidebarBackgroundOpacity.getValue().floatValue());
            int titleBackgroundOpacity = this.minecraft.options.getBackgroundColor(config.sidebarBackgroundTitleOpacity.getValue().floatValue());
            int splitLine = m - length * this.getFont().lineHeight;
            context.fill(o - 2, splitLine - this.getFont().lineHeight - 1, p, splitLine - 1, titleBackgroundOpacity);
            context.fill(o - 2, splitLine - 1, p, m, backgroundOpacity);
            int titleColor = 0xFFFFFF + ((int) (ScoreboardHelperConfig.INSTANCE.sidebarTitleTextOpacity.getValue() * 256) << 24);
            int textColor = 0xFFFFFF + ((int) (ScoreboardHelperConfig.INSTANCE.sidebarTextOpacity.getValue() * 256) << 24);
            context.drawString(this.getFont(), text, o + finalJ / 2 - i / 2, splitLine - this.getFont().lineHeight, titleColor, false);
            for (int t = 0; t < length; ++t) {
                SidebarEntry sidebarEntry = sidebarEntries[t];
                int u = m - (length - t) * this.getFont().lineHeight;
                context.drawString(this.getFont(), sidebarEntry.name(), o, u, textColor, false);
                if (config.sidebarScoreShown.getValue()) {
                    context.drawString(this.getFont(), sidebarEntry.score(), p - sidebarEntry.scoreWidth(), u, textColor, false);
                }
            }
        });
    }

}
