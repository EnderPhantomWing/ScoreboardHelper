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
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
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

@Mixin(value = Hud.class, priority = 10)
public abstract class HudMixin {

    @Shadow public abstract Font getFont();

    @Shadow @Final private Minecraft minecraft;

    /**
     * @author xiaozhu_zhizui
     * @reason 重写侧边栏渲染方法，以适用配置
     */
    @Overwrite
    private void displayScoreboardSidebar(GuiGraphicsExtractor guiGraphics, Objective objective) {
        ScoreboardHelperConfig config = ScoreboardHelperConfig.INSTANCE;
        if (!config.scoreboardShown.getValue()) {
            return;
        }

        Scoreboard scoreboard = objective.getScoreboard();
        NumberFormat numberFormat = objective.numberFormatOrDefault(StyledFormat.SIDEBAR_DEFAULT);

        Comparator<PlayerScoreEntry> comparatorString =
                Comparator.comparing(entry -> entry.ownerName().tryCollapseToString(), String.CASE_INSENSITIVE_ORDER);
        Comparator<PlayerScoreEntry> comparator = switch (config.sortingMethod.getValue()) {
            case BY_SCORE_DESC -> Comparator.comparing(PlayerScoreEntry::value)
                    .reversed()
                    .thenComparing(PlayerScoreEntry::owner, String.CASE_INSENSITIVE_ORDER);
            case BY_SCORE_ASC  -> Comparator.comparing(PlayerScoreEntry::value)
                    .thenComparing(PlayerScoreEntry::owner, String.CASE_INSENSITIVE_ORDER);
            case BY_NAME_DESC  -> comparatorString.reversed().thenComparing(PlayerScoreEntry::value);
            case BY_NAME_ASC   -> comparatorString.thenComparing(PlayerScoreEntry::value);
        };

        Collection<PlayerScoreEntry> entries = scoreboard.listPlayerScores(objective);
        int maxShow = config.maxShowCount.getValue();
        int total = entries.size();
        int page = Constants.PAGE; // 请确保该变量已定义（例如静态 int 或通过其他方式传入）
        // 安全计算 skip，避免越界或负数
        int skip = Math.max(0, Math.min(page, Math.max(0, total - maxShow)));

        SidebarEntry[] sidebarEntries = entries.stream()
                .filter(entry -> !entry.isHidden())
                .sorted(comparator)
                .skip(skip)
                .limit(maxShow)
                .map(scoreEntry -> {
                    PlayerTeam team = scoreboard.getPlayersTeam(scoreEntry.owner());
                    Component nameComponent = scoreEntry.ownerName();
                    MutableComponent formattedName = PlayerTeam.formatNameForTeam(team, nameComponent);
                    MutableComponent scoreText = scoreEntry.formatValue(numberFormat);
                    int scoreWidth = this.getFont().width(scoreText);
                    return new SidebarEntry(formattedName, scoreText, scoreWidth);
                })
                .toArray(SidebarEntry[]::new);

        Component title = objective.getDisplayName();
        int titleWidth = this.getFont().width(title);
        int maxWidth = titleWidth;
        int colonWidth = this.getFont().width(": ");
        boolean showScore = config.sidebarScoreShown.getValue();

        for (SidebarEntry entry : sidebarEntries) {
            int entryWidth = this.getFont().width(entry.name());
            if (showScore && entry.scoreWidth() > 0) {
                entryWidth += colonWidth + entry.scoreWidth();
            }
            maxWidth = Math.max(maxWidth, entryWidth);
        }
        int finalMaxWidth = maxWidth;

        // 计算垂直和水平起始位置
        int length = sidebarEntries.length;
        int lineHeight = this.getFont().lineHeight;
        int verticalPos = switch (config.sidebarPosition.getValue()) {
            case LEFT, RIGHT                 -> guiGraphics.guiHeight() / 2 + length * 3;
            case LEFT_UPPER_CORNER, RIGHT_UPPER_CORNER -> (length + 1) * lineHeight + 2;
            case LEFT_LOWER_CORNER, RIGHT_LOWER_CORNER -> guiGraphics.guiHeight() - 2;
        };
        verticalPos += config.sidebarYOffset.getValue();

        int horizontalPos = switch (config.sidebarPosition.getValue()) {
            case LEFT, LEFT_LOWER_CORNER, LEFT_UPPER_CORNER -> 5;
            case RIGHT, RIGHT_LOWER_CORNER, RIGHT_UPPER_CORNER -> guiGraphics.guiWidth() - finalMaxWidth - 3;
        };
        int rightBound = horizontalPos + finalMaxWidth + 2;

        int bgOpacity = this.minecraft.options.getBackgroundColor(config.sidebarBackgroundOpacity.getValue().floatValue());
        int titleBgOpacity = this.minecraft.options.getBackgroundColor(config.sidebarBackgroundTitleOpacity.getValue().floatValue());
        int splitLine = verticalPos - length * lineHeight;

        guiGraphics.fill(horizontalPos - 2, splitLine - lineHeight - 1, rightBound, splitLine - 1, titleBgOpacity);
        guiGraphics.fill(horizontalPos - 2, splitLine - 1, rightBound, verticalPos, bgOpacity);

        int titleColor = (0xFFFFFF) | ((int) (config.sidebarTitleTextOpacity.getValue() * 255) << 24);
        int textColor  = (0xFFFFFF) | ((int) (config.sidebarTextOpacity.getValue() * 255) << 24);

        guiGraphics.text(
                this.getFont(),
                title,
                horizontalPos + finalMaxWidth / 2 - titleWidth / 2,
                splitLine - lineHeight,
                titleColor,
                false
        );

        for (int i = 0; i < length; i++) {
            SidebarEntry entry = sidebarEntries[i];
            int y = verticalPos - (length - i) * lineHeight;
            guiGraphics.text(this.getFont(), entry.name(), horizontalPos, y, textColor, false);
            if (showScore) {
                guiGraphics.text(
                        this.getFont(),
                        entry.score(),
                        rightBound - entry.scoreWidth(),
                        y,
                        textColor,
                        false
                );
            }
        }
    }
}
