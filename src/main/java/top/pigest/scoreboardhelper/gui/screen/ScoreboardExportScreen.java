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

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.Objective;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Tuple;
import top.pigest.scoreboardhelper.ScoreboardHelper;
import top.pigest.scoreboardhelper.gui.widget.ScoreboardExportListWidget;
import top.pigest.scoreboardhelper.util.ScoreboardHelperUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ScoreboardExportScreen extends Screen {
    public static final ScoreboardExportScreen INSTANCE = new ScoreboardExportScreen(null);
    private Screen parent;
    private final List<RecordEntry> entries = new ArrayList<>();
    private ScoreboardExportListWidget scoreboardExportListWidget;

    public ScoreboardExportScreen(Screen parent) {
        super(Component.translatable(getTranslationKey("title")));
        this.parent = parent;
    }

    public void setParent(Screen parent) {
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.scoreboardExportListWidget = new ScoreboardExportListWidget(minecraft, this);
        addWidget(this.scoreboardExportListWidget);

        addRenderableWidget(new Button.Builder(Component.translatable(getTranslationKey("record")), button -> {
            boolean returnVal = record();
            if (!returnVal) {
                onClose();
            }
        }).size(200, 20).pos(width / 2 - 10 - 200, height - 40 - 30).build());
        addRenderableWidget(new Button.Builder(Component.translatable(getTranslationKey("direct")), button -> {
            tryExport();
            onClose();
        }).size(200, 20).pos(width / 2 - 10 - 200, height - 40).build());
        addRenderableWidget(new Button.Builder(Component.translatable(getTranslationKey("finish")), button -> {
            exportAll();
            onClose();
        }).size(200, 20).pos(width / 2 + 10, height - 40 - 30).build());
        addRenderableWidget(new Button.Builder(Component.translatable(getTranslationKey("close")), button -> onClose()).size(200, 20).pos(width / 2 + 10, height - 40).build());
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.scoreboardExportListWidget.render(context, mouseX, mouseY, delta);
        int TITLE_Y = 8;
        context.drawCenteredString(font, title, width / 2, TITLE_Y, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        Objects.requireNonNull(minecraft).setScreen(parent);
    }

    private static String getTranslationKey(String key) {
        return "options.scoreboard-helper.export." + key;
    }

    public void refresh() {
        this.rebuildWidgets();
    }

    private boolean record() {
        if (minecraft != null && minecraft.player != null) {
            Scoreboard scoreboard = minecraft.player.getScoreboard();
            Objective scoreboardObjective = ScoreboardHelperUtils.getSidebarObjective(scoreboard, minecraft.player);
            if(scoreboardObjective == null) {
                //#if MC >= 1.21.3
                //$$ minecraft.player.displayClientMessage(Component.translatable("hint.scoreboard-helper.export.fail.inactive").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
                //#else
                minecraft.player.sendSystemMessage(Component.translatable("hint.scoreboard-helper.export.fail.inactive").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                //#endif
                return false;
            } else {
                List<PlayerScoreEntry> scoreboardEntries = new ArrayList<>(scoreboard.listPlayerScores(scoreboardObjective));
                RecordEntry entry = new RecordEntry(scoreboardObjective.getDisplayName());
                for(PlayerScoreEntry entry1: scoreboardEntries) {
                    entry.scores.add(new Tuple<>(entry1.owner(), entry1.value()));
                }
                entries.removeIf(entry1 -> entry1.displayName.equals(scoreboardObjective.getDisplayName()));
                entries.add(entry);
                this.refresh();
                return true;
            }
        }
        return false;
    }

    private void tryExport() {
        if (minecraft != null && minecraft.player != null) {
            Scoreboard scoreboard = minecraft.player.getScoreboard();
            Objective scoreboardObjective = ScoreboardHelperUtils.getSidebarObjective(scoreboard, minecraft.player);
            if(scoreboardObjective == null) {
                //#if MC >= 1.21.3
                //$$ minecraft.player.displayClientMessage(Component.translatable("hint.scoreboard-helper.export.fail.inactive").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
                minecraft.player.sendSystemMessage(Component.translatable("hint.scoreboard-helper.export.fail.inactive").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                //#endif
            } else {
                export(scoreboardObjective, scoreboard);
            }
        }
    }

    private void export(Objective scoreboardObjective, Scoreboard scoreboard) {
        List<PlayerScoreEntry> scores = new ArrayList<>(scoreboard.listPlayerScores(scoreboardObjective));
        String name = scoreboardObjective.getName() + ".csv";
        Path path = FabricLoader.getInstance().getGameDir().resolve("scoreboard-exports");
        File file = path.resolve(name).toFile();
        if(!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                if (minecraft != null && minecraft.player != null) {
                    //# if MC >= 1.21.3
                    //$$ minecraft.player.displayClientMessage(Component.translatable("hint.scoreboard-helper.export.fail.exception").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
                    minecraft.player.sendSystemMessage(Component.translatable("hint.scoreboard-helper.export.fail.exception").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                    //#endif
                    ScoreboardHelper.LOGGER.error("Failed to export scoreboard", e);
                }
            }
        }
        try (FileWriter writer = new FileWriter(file)) {
            StringBuilder p = new StringBuilder();
            p.append(Component.translatable(getTranslationKey("chart.player")).getString())
                    .append(",")
                    .append(Component.translatable(getTranslationKey("chart.score")).getString())
                    .append("\n");
            for(int i = scores.size() - 1; i >= 0; i--) {
                PlayerScoreEntry score = scores.get(i);
                p.append(score.ownerName().tryCollapseToString()).append(",").append(score.value()).append("\n");
            }
            writer.write(String.valueOf(p));
            sendSuccessMessage(name, file);
        } catch (IOException e) {
            if (minecraft != null && minecraft.player != null) {
                //#if MC >= 1.21.3
                //$$ minecraft.player.displayClientMessage(Component.translatable("hint.scoreboard-helper.export.fail.exception").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
                minecraft.player.sendSystemMessage(Component.translatable("hint.scoreboard-helper.export.fail.exception").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                //#endif
                ScoreboardHelper.LOGGER.error("Failed to export scoreboard", e);
            }
        }
    }

    private void sendSuccessMessage(String name, File file) {
        Component text = Component.literal(name).setStyle(Style.EMPTY.withUnderlined(true).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath())));
        MutableComponent text1 = Component.translatable("hint.scoreboard-helper.export.success", text);
        if (minecraft != null) {
            if (minecraft.player != null) {
                minecraft.player.sendSystemMessage(text1);
            }
        }
    }

    private void exportAll() {
        if (minecraft != null && minecraft.player != null) {
            if (this.entries.isEmpty()) {
                //#if MC >= 1.21.3
                //$$ minecraft.player.displayClientMessage(Component.translatable("hint.scoreboard-player.export.fail.no_entry").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
                minecraft.player.sendSystemMessage(Component.translatable("hint.scoreboard-player.export.fail.no_entry").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                //#endif
            } else {
                Set<String> playerNames = new TreeSet<>();
                for(RecordEntry entry: entries) {
                    for(Tuple<String, Integer> pair: entry.scores) {
                        playerNames.add(pair.getA());
                    }
                }
                List<String> export = new ArrayList<>();
                StringBuilder head = new StringBuilder(Component.translatable(getTranslationKey("chart.player")).getString());
                for(RecordEntry entry: entries) {
                    head.append(",").append(entry.displayName.getString());
                }
                export.add(head.toString());
                for(String playerName: playerNames) {
                    StringBuilder s = new StringBuilder(playerName);
                    for(RecordEntry entry: entries) {
                        s.append(",");
                        Optional<Tuple<String, Integer>> optional = entry.scores.stream().filter(pair -> pair.getA().equals(playerName)).findFirst();
                        optional.ifPresent(stringIntegerPair -> s.append(stringIntegerPair.getB()));
                    }
                    export.add(s.toString());
                }
                String name = "EXPORT-" + UUID.randomUUID() + ".csv";
                Path path = FabricLoader.getInstance().getGameDir().resolve("scoreboard-exports");
                File file = path.resolve(name).toFile();
                if(!Files.exists(path)) {
                    try {
                        Files.createDirectories(path);
                    } catch (IOException e) {
                        //#if MC >= 1.21.3
                        //$$ minecraft.player.displayClientMessage(Component.translatable("hint.scoreboard-helper.export.fail.exception").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
                        minecraft.player.sendSystemMessage(Component.translatable("hint.scoreboard-helper.export.fail.exception").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                        //#endif
                        ScoreboardHelper.LOGGER.error("Failed to export scoreboard", e);
                    }
                }
                try (FileWriter writer = new FileWriter(file)) {
                    for (String s: export){
                        writer.write(s + "\n");
                    }
                    sendSuccessMessage(name, file);
                } catch (IOException e) {
                    if (minecraft != null && minecraft.player != null) {
                        //#if MC >= 1.21.3
                        //$$ minecraft.player.displayClientMessage(Component.translatable("hint.scoreboard-helper.export.fail.exception").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
                        minecraft.player.sendSystemMessage(Component.translatable("hint.scoreboard-helper.export.fail.exception").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                        ScoreboardHelper.LOGGER.error("Failed to export scoreboard", e);
                    }
                }
            }
        }
    }

    public List<RecordEntry> getRecordEntries() {
        return entries;
    }

    public static class RecordEntry {
        private final Component displayName;
        public final List<Tuple<String, Integer>> scores = new ArrayList<>();

        public RecordEntry(Component displayName) {
            this.displayName = displayName;
        }

        public Component getDisplayName() {
            return displayName;
        }
    }
}
