package top.pigest.scoreboardhelper.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.Objects;

public class ScoreboardHelperInfoScreen extends Screen {
    private final Screen parent;
    private final Component errorMessage;

    public ScoreboardHelperInfoScreen(Screen parent, Component errorMessage) {
        this(parent, InfoType.INFO, errorMessage);
    }

    public ScoreboardHelperInfoScreen(Screen parent, InfoType type, Component errorMessage) {
        super(type.title);
        this.parent = parent;
        this.errorMessage = errorMessage;
    }

    @Override
    public void onClose() {
        Objects.requireNonNull(minecraft).setScreen(parent);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new Button.Builder(Component.translatable("gui.back"), button -> onClose()).bounds(width / 2 - 60, height / 2 + 40, 120, 20).build());
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(font, title, width / 2, height / 2 - 50, 0xFFFFFF);
        context.drawCenteredString(font, errorMessage, width / 2, height / 2 - 10, 0xFFFFFF);
    }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
        parent.renderBackground(context, mouseX, mouseY, delta);
    }

    public enum InfoType {
        ERROR(Component.translatable("hint.scoreboard-helper.error").setStyle(Style.EMPTY.withColor(ChatFormatting.RED).withBold(true))),
        INFO(Component.translatable("hint.scoreboard-helper.info").setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA).withBold(true)));

        private final Component title;
        InfoType(Component title) {
            this.title = title;
        }
    }
}
