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

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 32;
    }

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

        @Override
        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.widgets.forEach(
                    widget -> {
                        widget.setY(y);
                        widget.render(context, mouseX, mouseY, tickDelta);
                    }
            );
        }
    }
}
