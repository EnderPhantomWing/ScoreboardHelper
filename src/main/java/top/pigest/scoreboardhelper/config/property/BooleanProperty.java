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
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import top.pigest.scoreboardhelper.util.TranslationKeyType;

public class BooleanProperty extends BaseProperty<Boolean> {

    public BooleanProperty(String key, Boolean defValue) {
        super(key, defValue);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public void fromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            setValue(jsonElement.getAsBoolean());
        } else {
            throw new JsonParseException("Json must be a primitive.");
        }
    }

    @Override
    public AbstractWidget createWidget(int x, int y, int width) {
        Component text = Component.translatable(Property.getTranslationKey(this.getKey(), TranslationKeyType.NORMAL));
        Component tooltip = Component.translatable(Property.getTranslationKey(this.getKey(), TranslationKeyType.TOOLTIP));
        Boolean[] values = new Boolean[] {true, false};
        CycleButton<Boolean> positionCyclingButtonWidget = CycleButton.<Boolean>builder(value -> value ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF
                //#if MC >= 1.21.11
                //$$ , this.getValue()
                //#endif
        ).withValues(values)
                //#if MC < 1.21.11
                .withInitialValue(this.getValue())
                //#endif
                .create(x, y, width, 20, text, (button, value) -> this.setValue(value));
        positionCyclingButtonWidget.setTooltip(Tooltip.create(tooltip));
        return positionCyclingButtonWidget;
    }
}
