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

package top.pigest.scoreboardhelper.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Objective;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.NotNull;
import top.pigest.scoreboardhelper.config.ScoreSortingMethod;
import top.pigest.scoreboardhelper.config.ScoreboardHelperConfig;
import top.pigest.scoreboardhelper.util.ScoreboardHelperUtils;
import top.pigest.scoreboardhelper.gui.widget.EditingScoreListWidget;
import top.pigest.scoreboardhelper.util.ScoreModification;

import java.util.*;
import java.util.stream.Collectors;

public class ScoreEditingScreen extends Screen {
    private final Screen parent;
    private final List<SingleScore> scores;
    private SortMethod sortMethod;
    private boolean reversedSort;
    private EditingScoreListWidget widget;
    private Button submitButton;
    private Button reversedSortButton;
    public Button addButton;
    public ScoreEditingScreen(Screen parent, @NotNull Scoreboard scoreboard, @NotNull Objective objective) {
        super(Component.translatable(getTranslationKey("title"), objective.getDisplayName()));
        this.parent = parent;
        this.scores = scoreboard.listPlayerScores(objective).stream().map(score -> new SingleScore(score.owner(), score.value())).collect(Collectors.toList());
        ScoreSortingMethod sorting = ScoreboardHelperConfig.INSTANCE.sortingMethod.getValue();
        reversedSort = switch (sorting) {
            case BY_SCORE_DESC, BY_NAME_DESC -> true;
            case BY_SCORE_ASC, BY_NAME_ASC -> false;
        };
        sortMethod = switch (sorting) {
            case BY_SCORE_ASC, BY_SCORE_DESC -> SortMethod.SCORE;
            case BY_NAME_DESC, BY_NAME_ASC -> SortMethod.NAME;
        };
    }

    @Override
    public void tick() {
        if (minecraft != null && minecraft.player != null) {
            if (!minecraft.player.hasPermissions(2)) {
                if (this.submitButton.active) {
                    this.submitButton.active = false;
                    this.submitButton.setTooltip(Tooltip.create(Component.translatable("hint.scoreboard-helper.edit_score.permission_denied")));
                }
            } else {
                if (!this.submitButton.active) {
                    this.submitButton.active = true;
                    this.submitButton.setTooltip(null);
                }
            }
        }
    }

    @Override
    protected void init() {
        CycleButton<SortMethod> buttonWidget = CycleButton.<SortMethod>builder(value -> Component.translatable("options.scoreboard-helper.edit_score.sort_method." + value.toString()))
                .withValues(SortMethod.values())
                .withInitialValue(sortMethod)
                .create(width / 2 - 10 - 200, height - 60, 170, 20, Component.translatable(getTranslationKey("sort_method")), ((button, value) -> {
                    this.sortMethod = value;
                    this.reversedSortButton.active = this.sortMethod != SortMethod.NONE;
                    this.widget.resort();
                }));
        this.widget = new EditingScoreListWidget(minecraft, this);
        addWidget(widget);
        addRenderableWidget(buttonWidget);
        this.reversedSortButton = new Button.Builder(this.reversedSort ? Component.literal("↑") : Component.literal("↓"), this::switchReversedSort).size(20, 20).pos(width / 2 - 10 - 20, height - 60).build();
        addRenderableWidget(this.reversedSortButton);
        this.addButton = new Button.Builder(Component.translatable(getTranslationKey("add")), this::addEntry).size(200, 20).pos(width / 2 + 10, height - 60).build();
        addRenderableWidget(this.addButton);
        addRenderableWidget(new Button.Builder(Component.translatable(getTranslationKey("close")), button -> onClose()).size(200, 20).pos(width / 2 + 10, height - 30).build());
        this.submitButton = new Button.Builder(Component.translatable(getTranslationKey("save")), button -> submit()).size(200, 20).pos(width / 2 - 10 - 200, height - 30).build();
        addRenderableWidget(this.submitButton);
    }

    public SortMethod getSortMethod() {
        return sortMethod;
    }

    public boolean isReversedSort() {
        return reversedSort;
    }

    private static String getTranslationKey(String key) {
        return "options.scoreboard-helper.edit_score." + key;
    }

    private void switchReversedSort(Button buttonWidget) {
        this.reversedSort = !this.reversedSort;
        if (this.reversedSort) {
            buttonWidget.setMessage(Component.literal("↑"));
        } else {
            buttonWidget.setMessage(Component.literal("↓"));
        }
        this.widget.resort();
    }

    private void addEntry(Button buttonWidget) {
        SingleScore entry = new SingleScore("", 0);
        this.scores.add(entry);
        this.widget.addSingleScore(entry);
        //#if MC >= 1.21.4
        //$$ this.widget.setScrollAmount(this.widget.maxScrollAmount());
        //#else
        this.widget.setScrollAmount(this.widget.getMaxScroll());
        //#endif
        this.addButton.active = false;
    }

    private void submit() {
        if (minecraft != null && minecraft.level != null && minecraft.player != null) {
            Scoreboard sb = minecraft.level.getScoreboard();
            Objective objective = ScoreboardHelperUtils.getSidebarObjective(sb, minecraft.player);
            List<ScoreModification> scoreModifications = getScoreModifications(sb, objective);
            for (ScoreModification m: scoreModifications) {
                minecraft.player.connection.sendCommand(m.getModificationCommand());
            }
            if (scoreModifications.isEmpty()) {
                //#if MC >= 1.21.3
                //$$ minecraft.gui.getChat().addMessage(Component.translatable("hint.scoreboard-helper.edit_score.fail.no_changes").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                //#else
                minecraft.player.sendSystemMessage(Component.translatable("hint.scoreboard-helper.edit_score.fail.no_changes").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                //#endif
            }
            onClose();
        }
    }

    @NotNull
    private List<ScoreModification> getScoreModifications(Scoreboard sb, Objective objective) {
        List<ScoreModification> scoreModifications = new ArrayList<>();
        List<SingleScore> list = sb.listPlayerScores(objective).stream().map(score -> new SingleScore(score.owner(), score.value())).toList();
        Map<String, SingleScore> map = new HashMap<>();
        for (SingleScore score: scores) {
            map.put(score.name, score);
        }
        for (SingleScore score: list) {
            SingleScore singleScore = map.get(score.name);
            if (singleScore != null) {
                if (singleScore.score != score.score) {
                    scoreModifications.add(new ScoreModification(ScoreModification.ModificationType.CHANGE, objective, singleScore.name, singleScore.score));
                }
                map.remove(score.name);
            } else {
                scoreModifications.add(new ScoreModification(ScoreModification.ModificationType.REMOVE, objective, score.name, 0));
            }
        }
        for (Map.Entry<String, SingleScore> entry: map.entrySet()) {
            if (!entry.getValue().name.isEmpty()) {
                scoreModifications.add(new ScoreModification(ScoreModification.ModificationType.ADD, objective, entry.getValue().name, entry.getValue().score));
            }
        }
        return scoreModifications;
    }

    @Override
    public void onClose() {
        Objects.requireNonNull(minecraft).setScreen(parent);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.widget.render(context, mouseX, mouseY, delta);
        int TITLE_Y = 8;
        context.drawCenteredString(font, title, width / 2, TITLE_Y, 0xFFFFFF);
    }

    public List<SingleScore> getScores() {
        return scores;
    }

    public enum SortMethod {
        NONE("none"),
        SCORE("score"),
        NAME("name");

        final String string;

        SortMethod(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    public static class SingleScore {
        private String name;
        private int score;
        public SingleScore(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getScore() {
            return score;
        }
    }
}
