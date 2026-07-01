package top.pigest.scoreboardhelper.gui.widget;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import top.pigest.scoreboardhelper.gui.screen.ScoreEditingScreen;
import top.pigest.scoreboardhelper.gui.screen.ScoreboardHelperInfoScreen;

import java.util.Comparator;
import java.util.List;

public class EditingScoreListWidget extends ContainerObjectSelectionList<EditingScoreListWidget.Entry> {
    private final ScoreEditingScreen parent;
    public EditingScoreListWidget(Minecraft minecraftClient, ScoreEditingScreen parent) {
        super(minecraftClient, parent.width + 25, parent.height - 32 - 70, 32, 25);
        this.parent = parent;
        for (ScoreEditingScreen.SingleScore entry: parent.getScores()) {
            this.addEntry(new Entry(entry));
        }
        resort();
    }

    public void addSingleScore(ScoreEditingScreen.SingleScore singleScore) {
        this.addEntry(new Entry(singleScore, true));
    }

    public void resort() {
        List<Entry> entries = this.children();
        Comparator<Entry> comparator = null;
        switch (parent.getSortMethod()) {
            case NONE -> {
                return;
            }
            case SCORE -> comparator = Comparator.comparingInt(value -> value.score);
            case NAME -> comparator = Comparator.comparing(entry -> entry.name, String::compareToIgnoreCase);
        }
        if (parent.isReversedSort()) {
            comparator = comparator.reversed();
        }
        entries.sort(comparator);
    }

    @Override
    public int getRowWidth() {
        return 300;
    }

    @Override
    protected int getScrollbarPosition() {
        return width / 2 + 130;
    }

    public class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        private final ScoreEditingScreen.SingleScore singleScore;
        private String name;
        private int score;
        private boolean isNewEntry;
        private final Button deleteButton;
        private final Button editButton;
        private final EditBox scoreField;
        private final EditBox nameField;

        public Entry(ScoreEditingScreen.SingleScore entry) {
            this(entry, false);
        }

        public Entry(ScoreEditingScreen.SingleScore entry, boolean isNewEntry) {
            this.singleScore = entry;
            this.name = entry.getName();
            this.score = entry.getScore();
            this.isNewEntry = isNewEntry;
            this.deleteButton = Button.builder(Component.translatable("options.scoreboard-helper.edit_score.delete"), button -> {
                remove(entry);
                if (isNewEntry) {
                    parent.addButton.active = true;
                }
            }).size(60, 20).build();
            this.editButton = Button.builder(Component.literal("✏"), this::onEditButtonClick).size(20, 20).build();
            this.scoreField = new EditBox(minecraft.font, 80, 20, Component.empty()) {
                @Override
                public void setFocused(boolean focused) {
                    super.setFocused(focused);
                    if (!focused) {
                        this.setValue(String.valueOf(score));
                    }
                }
            };
            this.scoreField.setValue(String.valueOf(score));
            this.scoreField.setResponder(s -> {
                try {
                    this.score = Integer.parseInt(s);
                    this.singleScore.setScore(this.score);
                    EditingScoreListWidget.this.resort();
                } catch (NumberFormatException ignored) {

                }
            });
            this.nameField = new EditBox(minecraft.font, 100, 20, Component.empty());
            this.nameField.setValue(name);
            this.nameField.setVisible(false);
            if (isNewEntry) {
                onEditButtonClick(this.editButton);
            }
        }

        private void remove(ScoreEditingScreen.SingleScore entry) {
            EditingScoreListWidget widget = EditingScoreListWidget.this;
            widget.removeEntry(this);
            widget.parent.getScores().remove(entry);
            if(widget.getScrollAmount() > widget.getMaxScroll()) {
                widget.setScrollAmount(widget.getMaxScroll());
            }
        }

        private void removeIfNew() {
            if (this.isNewEntry) {
                remove(singleScore);
            }
        }

        private void onEditButtonClick(Button button) {
            EditingScoreListWidget widget = EditingScoreListWidget.this;
            this.nameField.setVisible(!this.nameField.visible);
            button.setMessage(this.nameField.visible ? Component.literal("✔") : Component.literal("✏"));
            if (!this.nameField.visible) {
                if (this.nameField.getValue().isEmpty()) {
                    minecraft.setScreen(new ScoreboardHelperInfoScreen(EditingScoreListWidget.this.parent, ScoreboardHelperInfoScreen.InfoType.ERROR, Component.translatable("hint.scoreboard-helper.edit_score.fail.name_is_empty")));
                    this.removeIfNew();
                } else if (this.nameField.getValue().contains(" ")) {
                    minecraft.setScreen(new ScoreboardHelperInfoScreen(EditingScoreListWidget.this.parent, ScoreboardHelperInfoScreen.InfoType.ERROR, Component.translatable("hint.scoreboard-helper.edit_score.fail.no_space_in_name")));
                    this.removeIfNew();
                } else if (!widget.children().stream().filter(entry -> entry.name.equals(this.nameField.getValue()) && entry != this).toList().isEmpty()) {
                    minecraft.setScreen(new ScoreboardHelperInfoScreen(EditingScoreListWidget.this.parent, ScoreboardHelperInfoScreen.InfoType.ERROR, Component.translatable("hint.scoreboard-helper.edit_score.fail.name_exist")));
                    this.removeIfNew();
                } else {
                    this.name = this.nameField.getValue();
                    this.singleScore.setName(this.name);
                    this.isNewEntry = false;
                    widget.parent.addButton.active = true;
                    widget.resort();
                }
            } else {
                resetEditState(widget);
            }
        }

        private void resetEditState(EditingScoreListWidget widget) {
            List<Entry> entries = widget.children().stream().filter(entry -> entry.nameField.visible && entry != this).toList();
            for (var entry: entries) {
                entry.nameField.setVisible(false);
                entry.editButton.setMessage(Component.literal("✏"));
            }
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(editButton, nameField, scoreField, deleteButton);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(editButton, nameField, scoreField, deleteButton);
        }

        @Override
        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if (!this.nameField.visible) {
                Component text = Component.nullToEmpty(this.name);
                context.drawString(EditingScoreListWidget.this.minecraft.font, text, x + 20 + 5, y + entryHeight / 2 - EditingScoreListWidget.this.minecraft.font.lineHeight / 2, 0xFFFFFF, false);
            }
            this.nameField.setPosition(x + 20 + 5, y);
            this.nameField.render(context, mouseX, mouseY, tickDelta);
            this.editButton.setPosition(x, y);
            this.editButton.render(context, mouseX, mouseY, tickDelta);
            this.scoreField.setPosition(x + 130, y);
            this.scoreField.render(context, mouseX, mouseY, tickDelta);
            this.deleteButton.setPosition(x + 130 + 80 + 5, y);
            this.deleteButton.render(context, mouseX, mouseY, tickDelta);
        }
    }
}
