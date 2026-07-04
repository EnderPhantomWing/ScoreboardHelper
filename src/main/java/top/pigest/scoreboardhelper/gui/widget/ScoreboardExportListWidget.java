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

package top.pigest.scoreboardhelper.gui.widget;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.network.chat.Component;
import top.pigest.scoreboardhelper.gui.screen.ScoreboardExportScreen;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ScoreboardExportListWidget extends ContainerObjectSelectionList<ScoreboardExportListWidget.Entry> {
    private final ScoreboardExportScreen parent;

    public ScoreboardExportListWidget(Minecraft minecraftClient, ScoreboardExportScreen parent) {
        super(minecraftClient, parent.width + 20, parent.height - 32 - 80, 32, 25);
        this.parent = parent;
        for(ScoreboardExportScreen.RecordEntry entry: parent.getRecordEntries()) {
            this.addEntry(new Entry(entry));
        }
    }

    public class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        private final ScoreboardExportScreen.RecordEntry entry;
        private final Component displayName;
        private final Button deleteButton;
        private final Button forwardButton;
        private final Button backwardButton;

        Entry(ScoreboardExportScreen.RecordEntry entry) {
            List<ScoreboardExportScreen.RecordEntry> recordEntries = ScoreboardExportListWidget.this.parent.getRecordEntries();
            this.entry = entry;
            this.displayName = entry.getDisplayName();
            this.deleteButton = Button.builder(Component.translatable("options.scoreboard-helper.export.delete"), button -> {
                ScoreboardExportListWidget widget = ScoreboardExportListWidget.this;
                int index = getIndex();
                int size = ScoreboardExportListWidget.this.children().size();
                if(index == 0 && size > 1) {
                    widget.children().get(1).setForwardButtonActive(false);
                }
                if(index == 1 && size == 2) {
                    widget.children().get(0).setBackwardButtonActive(false);
                }
                if(index == size - 1 && size > 1) {
                    widget.children().get(size - 2).setBackwardButtonActive(false);
                }
                widget.parent.getRecordEntries().remove(entry);
                widget.removeEntry(this);
                //#if MC >= 1.21.4
                //$$ if (widget.scrollAmount() > widget.maxScrollAmount()) {
                //$$     widget.setScrollAmount(widget.maxScrollAmount());
                //$$ }
                //#else
                if (widget.getScrollAmount() > widget.getMaxScroll()) {
                    widget.setScrollAmount(widget.getMaxScroll());
                }
                //#endif
            }).bounds(0, 0, 60, 20).build();
            this.forwardButton = Button.builder(Component.literal("↑"), button -> {
                int index = getIndex();
                int size = ScoreboardExportListWidget.this.children().size();
                if(index > 0) {
                    Entry entry1 = ScoreboardExportListWidget.this.children().get(index - 1);
                    ScoreboardExportListWidget.this.children().set(index - 1, this);
                    ScoreboardExportListWidget.this.children().set(index, entry1);
                    ScoreboardExportScreen.RecordEntry recordEntry = recordEntries.get(index);
                    ScoreboardExportScreen.RecordEntry recordEntry1 = recordEntries.get(index - 1);
                    recordEntries.set(index - 1, recordEntry);
                    recordEntries.set(index, recordEntry1);
                    if(index == 1) {
                        setForwardButtonActive(false);
                        entry1.setForwardButtonActive(true);
                    }
                    if(index == size - 1) {
                        entry1.setBackwardButtonActive(false);
                    }
                }
                setBackwardButtonActive(true);
            }).bounds(0, 0, 20, 20).build();
            this.backwardButton = Button.builder(Component.literal("↓"), button -> {
                int index = getIndex();
                int size = ScoreboardExportListWidget.this.children().size();
                if(index < size - 1) {
                    Entry entry1 = ScoreboardExportListWidget.this.children().get(index + 1);
                    ScoreboardExportListWidget.this.children().set(index + 1, this);
                    ScoreboardExportListWidget.this.children().set(index, entry1);
                    ScoreboardExportScreen.RecordEntry recordEntry = recordEntries.get(index);
                    ScoreboardExportScreen.RecordEntry recordEntry1 = recordEntries.get(index + 1);
                    recordEntries.set(index + 1, recordEntry);
                    recordEntries.set(index, recordEntry1);
                    if(index == size - 2) {
                        setBackwardButtonActive(false);
                        entry1.setBackwardButtonActive(true);
                    }
                    if(index == 0) {
                        entry1.setForwardButtonActive(false);
                    }
                }
                setForwardButtonActive(true);
            }).bounds(0, 0, 20, 20).build();
            setForwardButtonActive(false);
            setBackwardButtonActive(false);
            int index = getIndex();
            if(index > 0) {
                Entry entry1 = ScoreboardExportListWidget.this.children().get(index - 1);
                entry1.setBackwardButtonActive(true);
                setForwardButtonActive(true);
            }
        }

        private void setForwardButtonActive(boolean active) {
            this.forwardButton.active = active;
        }

        private void setBackwardButtonActive(boolean active) {
            this.backwardButton.active = active;
        }


        private int getIndex() {
            ScoreboardExportListWidget widget = ScoreboardExportListWidget.this;
            return widget.parent.getRecordEntries().indexOf(entry);
        }


        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.deleteButton, this.forwardButton, this.backwardButton);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.deleteButton, this.forwardButton, this.backwardButton);
        }

        //#if MC >= 1.21.10
        //$$ //#if MC >= 26.1
        //$$ //$$ @Override
        //$$ //$$ public void extractContent(GuiGraphicsExtractor context, int index, int y, boolean hovered, float tickDelta) {
        //$$ //$$     int x = this.getX();
        //$$ //$$     int entryWidth = this.getWidth();
        //$$ //$$     int entryHeight = this.getHeight();
        //$$ //$$     double s = ScoreboardExportListWidget.this.minecraft.getWindow().getGuiScale();
        //$$ //$$     int mouseX = (int)(ScoreboardExportListWidget.this.minecraft.mouseHandler.xpos() / s);
        //$$ //$$     int mouseY = (int)(ScoreboardExportListWidget.this.minecraft.mouseHandler.ypos() / s);
        //$$ //$$     context.text(ScoreboardExportListWidget.this.minecraft.font, this.displayName, x - 40, y + entryHeight / 2 - ScoreboardExportListWidget.this.minecraft.font.lineHeight / 2, 0xFFFFFF, false);
        //$$ //$$     this.deleteButton.setX(x + 90);
        //$$ //$$     this.deleteButton.setY(y);
        //$$ //$$     this.deleteButton.extractRenderState(context, mouseX, mouseY, tickDelta);
        //$$ //$$     this.forwardButton.setX(x + 90 + 60 + 5);
        //$$ //$$     this.forwardButton.setY(y);
        //$$ //$$     this.forwardButton.extractRenderState(context, mouseX, mouseY, tickDelta);
        //$$ //$$     this.backwardButton.setX(x + 90 + 60 + 5 + 20 + 5);
        //$$ //$$     this.backwardButton.setY(y);
        //$$ //$$     this.backwardButton.extractRenderState(context, mouseX, mouseY, tickDelta);
        //$$ //$$ }
        //$$ //#else
        //$$ @Override
        //$$ public void renderContent(GuiGraphics context, int index, int y, boolean hovered, float tickDelta) {
        //$$     int x = this.getX();
        //$$     int entryWidth = this.getWidth();
        //$$     int entryHeight = this.getHeight();
        //$$     double s = ScoreboardExportListWidget.this.minecraft.getWindow().getGuiScale();
        //$$     int mouseX = (int)(ScoreboardExportListWidget.this.minecraft.mouseHandler.xpos() / s);
        //$$     int mouseY = (int)(ScoreboardExportListWidget.this.minecraft.mouseHandler.ypos() / s);
        //$$     context.drawString(ScoreboardExportListWidget.this.minecraft.font, this.displayName, x - 40, y + entryHeight / 2 - ScoreboardExportListWidget.this.minecraft.font.lineHeight / 2, 0xFFFFFF, false);
        //$$     this.deleteButton.setX(x + 90);
        //$$     this.deleteButton.setY(y);
        //$$     this.deleteButton.render(context, mouseX, mouseY, tickDelta);
        //$$     this.forwardButton.setX(x + 90 + 60 + 5);
        //$$     this.forwardButton.setY(y);
        //$$     this.forwardButton.render(context, mouseX, mouseY, tickDelta);
        //$$     this.backwardButton.setX(x + 90 + 60 + 5 + 20 + 5);
        //$$     this.backwardButton.setY(y);
        //$$     this.backwardButton.render(context, mouseX, mouseY, tickDelta);
        //$$ }
        //$$ //#endif
        //#else
        @Override
        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawString(ScoreboardExportListWidget.this.minecraft.font, this.displayName, x - 40, y + entryHeight / 2 - ScoreboardExportListWidget.this.minecraft.font.lineHeight / 2, 0xFFFFFF, false);
            this.deleteButton.setX(x + 90);
            this.deleteButton.setY(y);
            this.deleteButton.render(context, mouseX, mouseY, tickDelta);
            this.forwardButton.setX(x + 90 + 60 + 5);
            this.forwardButton.setY(y);
            this.forwardButton.render(context, mouseX, mouseY, tickDelta);
            this.backwardButton.setX(x + 90 + 60 + 5 + 20 + 5);
            this.backwardButton.setY(y);
            this.backwardButton.render(context, mouseX, mouseY, tickDelta);
        }
        //#endif
    }
}
