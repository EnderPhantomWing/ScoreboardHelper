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

package top.pigest.scoreboardhelper.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import top.pigest.scoreboardhelper.config.property.Property;
import top.pigest.scoreboardhelper.util.TranslationKeyType;

public class ClothConfigScreenFactory {
    public static Screen create(Screen parent) {
        ScoreboardHelperConfig config = ScoreboardHelperConfig.INSTANCE;
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable(Property.getTranslationKey("title", TranslationKeyType.NORMAL)))
                .setSavingRunnable(config::save);

        ConfigEntryBuilder eb = builder.entryBuilder();

        // Category: General
        ConfigCategory general = builder.getOrCreateCategory(
                Component.translatable(Property.getTranslationKey("category.general", TranslationKeyType.NORMAL)));

        general.addEntry(eb.startBooleanToggle(
                Component.translatable(Property.getTranslationKey("show_scoreboard", TranslationKeyType.NORMAL)),
                config.scoreboardShown.getValue())
                .setDefaultValue(config.scoreboardShown.getDefaultValue())
                .setTooltip(Component.translatable(Property.getTranslationKey("show_scoreboard", TranslationKeyType.TOOLTIP)))
                .setSaveConsumer(config.scoreboardShown::setValue)
                .build());

        general.addEntry(eb.startBooleanToggle(
                Component.translatable(Property.getTranslationKey("show_sidebar_score", TranslationKeyType.NORMAL)),
                config.sidebarScoreShown.getValue())
                .setDefaultValue(config.sidebarScoreShown.getDefaultValue())
                .setTooltip(Component.translatable(Property.getTranslationKey("show_sidebar_score", TranslationKeyType.TOOLTIP)))
                .setSaveConsumer(config.sidebarScoreShown::setValue)
                .build());

        general.addEntry(eb.startEnumSelector(
                Component.translatable(Property.getTranslationKey("score_sorting_method", TranslationKeyType.NORMAL)),
                ScoreSortingMethod.class, config.sortingMethod.getValue())
                .setDefaultValue(config.sortingMethod.getDefaultValue())
                .setTooltip(Component.translatable(Property.getTranslationKey("score_sorting_method", TranslationKeyType.TOOLTIP)))
                .setEnumNameProvider(value -> Component.translatable(
                        Property.getTranslationKey("score_sorting_method", TranslationKeyType.NORMAL) + ".value." + value.toString()))
                .setSaveConsumer(config.sortingMethod::setValue)
                .build());

        general.addEntry(eb.startIntSlider(
                Component.translatable(Property.getTranslationKey("max_show_count", TranslationKeyType.NORMAL)),
                config.maxShowCount.getValue(), 0, 100)
                .setDefaultValue(config.maxShowCount.getDefaultValue())
                .setTooltip(Component.translatable(Property.getTranslationKey("max_show_count", TranslationKeyType.TOOLTIP)))
                .setSaveConsumer(config.maxShowCount::setValue)
                .build());

        general.addEntry(eb.startBooleanToggle(
                Component.translatable(Property.getTranslationKey("default_team_chat", TranslationKeyType.NORMAL)),
                config.defaultTeamChat.getValue())
                .setDefaultValue(config.defaultTeamChat.getDefaultValue())
                .setTooltip(Component.translatable(Property.getTranslationKey("default_team_chat", TranslationKeyType.TOOLTIP)))
                .setSaveConsumer(config.defaultTeamChat::setValue)
                .build());

        // Category: Sidebar Appearance
        ConfigCategory appearance = builder.getOrCreateCategory(
                Component.translatable(Property.getTranslationKey("category.appearance", TranslationKeyType.NORMAL)));

        appearance.addEntry(eb.startEnumSelector(
                Component.translatable(Property.getTranslationKey("sidebar_position", TranslationKeyType.NORMAL)),
                ScoreboardSidebarPosition.class, config.sidebarPosition.getValue())
                .setDefaultValue(config.sidebarPosition.getDefaultValue())
                .setTooltip(Component.translatable(Property.getTranslationKey("sidebar_position", TranslationKeyType.TOOLTIP)))
                .setEnumNameProvider(value -> Component.translatable(
                        Property.getTranslationKey("sidebar_position", TranslationKeyType.NORMAL) + ".value." + value.toString()))
                .setSaveConsumer(config.sidebarPosition::setValue)
                .build());

        appearance.addEntry(eb.startIntSlider(
                Component.translatable(Property.getTranslationKey("sidebar_y_offset", TranslationKeyType.NORMAL)),
                config.sidebarYOffset.getValue(), -100, 100)
                .setDefaultValue(config.sidebarYOffset.getDefaultValue())
                .setTooltip(Component.translatable(Property.getTranslationKey("sidebar_y_offset", TranslationKeyType.TOOLTIP)))
                .setSaveConsumer(config.sidebarYOffset::setValue)
                .build());

        appearance.addEntry(eb.startIntSlider(
                Component.translatable(Property.getTranslationKey("sidebar_background_opacity", TranslationKeyType.NORMAL)),
                (int) (config.sidebarBackgroundOpacity.getValue() * 100), 0, 100)
                .setDefaultValue((int) (config.sidebarBackgroundOpacity.getDefaultValue() * 100))
                .setTooltip(Component.translatable(Property.getTranslationKey("sidebar_background_opacity", TranslationKeyType.TOOLTIP)))
                .setTextGetter(value -> Component.literal(value + "%"))
                .setSaveConsumer(value -> config.sidebarBackgroundOpacity.setValue(value / 100.0))
                .build());

        appearance.addEntry(eb.startIntSlider(
                Component.translatable(Property.getTranslationKey("sidebar_background_title_opacity", TranslationKeyType.NORMAL)),
                (int) (config.sidebarBackgroundTitleOpacity.getValue() * 100), 0, 100)
                .setDefaultValue((int) (config.sidebarBackgroundTitleOpacity.getDefaultValue() * 100))
                .setTooltip(Component.translatable(Property.getTranslationKey("sidebar_background_title_opacity", TranslationKeyType.TOOLTIP)))
                .setTextGetter(value -> Component.literal(value + "%"))
                .setSaveConsumer(value -> config.sidebarBackgroundTitleOpacity.setValue(value / 100.0))
                .build());

        appearance.addEntry(eb.startIntSlider(
                Component.translatable(Property.getTranslationKey("sidebar_text_opacity", TranslationKeyType.NORMAL)),
                (int) (config.sidebarTextOpacity.getValue() * 100), 10, 100)
                .setDefaultValue((int) (config.sidebarTextOpacity.getDefaultValue() * 100))
                .setTooltip(Component.translatable(Property.getTranslationKey("sidebar_text_opacity", TranslationKeyType.TOOLTIP)))
                .setTextGetter(value -> Component.literal(value + "%"))
                .setSaveConsumer(value -> config.sidebarTextOpacity.setValue(value / 100.0))
                .build());

        appearance.addEntry(eb.startIntSlider(
                Component.translatable(Property.getTranslationKey("sidebar_title_text_opacity", TranslationKeyType.NORMAL)),
                (int) (config.sidebarTitleTextOpacity.getValue() * 100), 10, 100)
                .setDefaultValue((int) (config.sidebarTitleTextOpacity.getDefaultValue() * 100))
                .setTooltip(Component.translatable(Property.getTranslationKey("sidebar_title_text_opacity", TranslationKeyType.TOOLTIP)))
                .setTextGetter(value -> Component.literal(value + "%"))
                .setSaveConsumer(value -> config.sidebarTitleTextOpacity.setValue(value / 100.0))
                .build());

        return builder.build();
    }
}
