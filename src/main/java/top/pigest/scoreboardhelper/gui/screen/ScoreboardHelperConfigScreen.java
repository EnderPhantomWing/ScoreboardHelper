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
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import top.pigest.scoreboardhelper.config.ScoreboardHelperConfig;
import top.pigest.scoreboardhelper.config.property.Property;
import top.pigest.scoreboardhelper.gui.widget.PropertyListWidget;
import top.pigest.scoreboardhelper.util.TranslationKeyType;

import java.util.Objects;

public class ScoreboardHelperConfigScreen extends Screen {
    private final Screen parent;
    private final ScoreboardHelperConfig config;
    private PropertyListWidget propertyList;

    public ScoreboardHelperConfigScreen(Screen parent, ScoreboardHelperConfig config) {
        super(Component.translatable(Property.getTranslationKey("title", TranslationKeyType.NORMAL)));
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
        this.propertyList = new PropertyListWidget(minecraft, this);
        addWidget(propertyList);
        addRenderableWidget(new Button.Builder(Component.translatable(Property.getTranslationKey("reset", TranslationKeyType.NORMAL)), button -> {
            ScoreboardHelperConfig.INSTANCE.resetDefault();
            this.rebuildWidgets();
        }).size(200, 20).pos(width / 2 + 10, height - 26).build());
        addRenderableWidget(new Button.Builder(CommonComponents.GUI_DONE, button -> onClose()).size(200, 20).pos(width / 2 - 10 - 200, height - 26).build());
    }

    public ScoreboardHelperConfig getConfig() {
        return config;
    }

    @Override
    public void onClose() {
        //#if MC < 26.2
        Objects.requireNonNull(minecraft).setScreen(parent);
        //#endif
    }

    @Override
    public void removed() {
        config.save();
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.propertyList.render(context, mouseX, mouseY, delta);
        int TITLE_Y = 8;
        //#if MC >= 1.21.8
        //$$ context.drawCenteredString(font, title.getVisualOrderText(), width / 2, TITLE_Y, 0xFFFFFF);
        //#else
        context.drawCenteredString(font, title, width / 2, TITLE_Y, 0xFFFFFF);
        //#endif
    }
}
