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
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import org.jetbrains.annotations.Nullable;
import top.pigest.scoreboardhelper.config.property.Property;
import top.pigest.scoreboardhelper.gui.screen.ScoreboardHelperConfigScreen;

import java.util.List;
import java.util.Map;

public class PropertyListWidget extends ContainerObjectSelectionList<PropertyListWidget.WidgetEntry> {
    private final ScoreboardHelperConfigScreen parent;
    public PropertyListWidget(Minecraft minecraftClient, ScoreboardHelperConfigScreen screen) {
        super(minecraftClient, screen.width, screen.height - 32 - 32, 32, 25);
        parent = screen;
        this.addAll(parent.getConfig().getProperties());
    }

    public void addPropertyEntry(Property<?> property1, @Nullable Property<?> property2) {
        this.addEntry(WidgetEntry.create(this.width, property1, property2));
    }

    public void addAll(List<Property<?>> properties) {
        for (int i = 0; i < properties.size(); i += 2) {
            addPropertyEntry(properties.get(i), i < properties.size() - 1 ? properties.get(i + 1) : null);
        }
    }

    @Override
    public int getRowWidth() {
        return 400;
    }

    //#if MC <= 1.21.1
    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 32;
    }
    //#endif

    protected static class WidgetEntry extends ContainerObjectSelectionList.Entry<WidgetEntry> {
        private final List<AbstractWidget> widgets;

        private WidgetEntry(Map<Property<?>, AbstractWidget> propertiesToWidgets) {
            this.widgets = ImmutableList.copyOf(propertiesToWidgets.values());
        }

        public static WidgetEntry create(int width, Property<?> firstProperty, @Nullable Property<?> secondProperty) {
            AbstractWidget clickableWidget = firstProperty.createWidget(width / 2 - 205, 0, 200);
            if (secondProperty == null) {
                return new WidgetEntry(ImmutableMap.of(firstProperty, clickableWidget));
            }
            return new WidgetEntry(ImmutableMap.of(firstProperty, clickableWidget, secondProperty, secondProperty.createWidget(width / 2 - 205 + 210, 0, 200)));
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return widgets;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return widgets;
        }

        //#if MC >= 1.21.10
        //$$ @Override
        //$$ public void renderContent(GuiGraphics context, int index, int y, boolean hovered, float tickDelta) {
        //$$     int mouseX = (int) Minecraft.getInstance().mouseHandler.xpos();
        //$$     int mouseY = (int) Minecraft.getInstance().mouseHandler.ypos();
        //$$     this.widgets.forEach(
        //$$             widget -> {
        //$$                 widget.setY(y);
        //$$                 widget.render(context, mouseX, mouseY, tickDelta);
        //$$             }
        //$$     );
        //$$ }
        //#else
        @Override
        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.widgets.forEach(
                    widget -> {
                        widget.setY(y);
                        widget.render(context, mouseX, mouseY, tickDelta);
                    }
            );
        }
        //#endif
    }
}
