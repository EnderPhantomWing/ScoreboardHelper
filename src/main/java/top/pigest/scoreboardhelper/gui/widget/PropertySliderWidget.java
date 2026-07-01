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
