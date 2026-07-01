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
