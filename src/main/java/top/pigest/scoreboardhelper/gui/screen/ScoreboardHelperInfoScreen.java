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
