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
        CycleButton<Boolean> positionCyclingButtonWidget = CycleButton.<Boolean>builder(value -> value ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF).withValues(values).withInitialValue(this.getValue())
                .create(x, y, width, 20, text, (button, value) -> this.setValue(value));
        positionCyclingButtonWidget.setTooltip(Tooltip.create(tooltip));
        return positionCyclingButtonWidget;
    }
}
