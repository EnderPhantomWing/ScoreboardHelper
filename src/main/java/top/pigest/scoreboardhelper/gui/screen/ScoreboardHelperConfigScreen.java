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
        Objects.requireNonNull(minecraft).setScreen(parent);
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
        context.drawCenteredString(font, title, width / 2, TITLE_Y, 0xFFFFFF);
    }
}
