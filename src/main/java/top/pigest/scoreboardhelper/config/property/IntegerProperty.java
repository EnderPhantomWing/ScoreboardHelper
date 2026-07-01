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

package top.pigest.scoreboardhelper.config.property;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import top.pigest.scoreboardhelper.gui.widget.PropertySliderWidget;
import top.pigest.scoreboardhelper.util.TranslationKeyType;

public class IntegerProperty extends BaseProperty<Integer> {
    private final int min;
    private final int max;
    public IntegerProperty(String key, Integer defValue, int min, int max) {
        super(key, defValue);
        this.min = min;
        this.max = max;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public void fromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            setValue(jsonElement.getAsInt());
        } else {
            throw new JsonParseException("Json must be primitive.");
        }
    }

    @Override
    public AbstractWidget createWidget(int x, int y, int width) {
        Component text = Component.translatable(Property.getTranslationKey(this.getKey(), TranslationKeyType.NORMAL));
        Component tooltip = Component.translatable(Property.getTranslationKey(this.getKey(), TranslationKeyType.TOOLTIP));
        int propertyValue = getValue();
        double value = 1.0 * (propertyValue - min) / (max - min);
        PropertySliderWidget<Integer> propertySliderWidget = new PropertySliderWidget<>(x, y, width, 20, text, value, this, PropertySliderWidget.ValueTextGetter.getDefaultTextGetter(), PropertySliderWidget.PropertyValueApplier.getDefaultIntegerPropertyValueApplier(min, max));
        propertySliderWidget.setTooltip(Tooltip.create(tooltip));
        return propertySliderWidget;
    }
}
