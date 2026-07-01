package top.pigest.scoreboardhelper.config.property;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import top.pigest.scoreboardhelper.util.TranslationKeyType;

import java.util.Arrays;
import java.util.Optional;

public class EnumProperty<T extends Enum<T>> extends BaseProperty<T> {
    public EnumProperty(String key, T defValue) {
        super(key, defValue);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(this.getValue().toString());
    }

    @Override
    public void fromJson(JsonElement jsonElement) {
        if(jsonElement.isJsonPrimitive()) {
            T[] values = this.getValue().getDeclaringClass().getEnumConstants();
            T value = getDefaultValue();
            Optional<T> optional = Arrays.stream(values).filter(val -> val.toString().equals(jsonElement.getAsString())).findFirst();
            if(optional.isPresent()) {
                value = optional.get();
            }
            setValue(value);
        } else {
            throw new JsonParseException("Json must be primitive.");
        }
    }

    @Override
    public AbstractWidget createWidget(int x, int y, int width) {
        Component text = Component.translatable(Property.getTranslationKey(this.getKey(), TranslationKeyType.NORMAL));
        Component tooltip = Component.translatable(Property.getTranslationKey(this.getKey(), TranslationKeyType.TOOLTIP));
        T[] values = this.getValue().getDeclaringClass().getEnumConstants();
        CycleButton<T> positionCyclingButtonWidget = CycleButton.<T>builder(value -> Component.translatable(Property.getTranslationKey(this.getKey(),
                        TranslationKeyType.NORMAL) + ".value." + value.toString())).withValues(values).withInitialValue(this.getValue())
                .create(x, y, width, 20, text, (button, value) -> this.setValue(value));
        positionCyclingButtonWidget.setTooltip(Tooltip.create(tooltip));
        return positionCyclingButtonWidget;
    }
}
