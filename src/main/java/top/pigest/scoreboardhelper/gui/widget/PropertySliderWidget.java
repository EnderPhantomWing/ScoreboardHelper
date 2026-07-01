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

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import top.pigest.scoreboardhelper.config.property.Property;

public class PropertySliderWidget<T extends Number> extends AbstractSliderButton {
    private final Component prefix;
    private final Property<T> property;
    private final ValueTextGetter<T> textGetter;
    private final PropertyValueApplier<T> valueApplier;

    public PropertySliderWidget(int x, int y, int width, int height, Component prefix, double value, Property<T> property, ValueTextGetter<T> textGetter, PropertyValueApplier<T> valueApplier) {
        super(x, y, width, height, textGetter.toText(prefix, property.getValue()), value);
        this.prefix = prefix;
        this.property = property;
        this.textGetter = textGetter;
        this.valueApplier = valueApplier;
    }

    @Override
    protected void updateMessage() {
        setMessage(textGetter.toText(prefix, property.getValue()));
    }

    public interface ValueTextGetter<T extends Number> {
        Component toText(Component prefix, T value);

        static <T extends Number> ValueTextGetter<T> getDefaultTextGetter() {
            return (prefix1, value1) -> Options.genericValueLabel(prefix1, Component.nullToEmpty(value1.toString()));
        }

        static ValueTextGetter<Double> getDefaultPercentTextGetter() {
            return (prefix1, value1) -> Component.translatable("options.percent_value", prefix1, (int)(value1 * 100.0));
        }
    }

    public interface PropertyValueApplier<T extends Number> {
        T applyValue(double value);

        static PropertyValueApplier<Double> getDefaultDoublePropertyValueApplier(double min, double max) {
            return value1 -> min + (max - min) * value1;
        }

        static PropertyValueApplier<Integer> getDefaultIntegerPropertyValueApplier(int min, int max) {
            return value1 -> (int) (min + (max - min) * value1);
        }
    }

    @Override
    protected void applyValue() {
        this.property.setValue(this.valueApplier.applyValue(this.value));
    }
}
