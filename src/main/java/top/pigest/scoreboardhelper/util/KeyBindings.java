package top.pigest.scoreboardhelper.util;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyMapping KEY_BINDING_PAGE_UP = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.scoreboard-helper.pageUp",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UP,
            "category.scoreboard-helper"
    ));
    public static final KeyMapping KEY_BINDING_PAGE_DOWN = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.scoreboard-helper.pageDown",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_DOWN,
            "category.scoreboard-helper"
    ));
    public static final KeyMapping KEY_BINDING_SWITCH_DISPLAY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.scoreboard-helper.switchDisplay",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "category.scoreboard-helper"
    ));
    public static final KeyMapping KEY_BINDING_OPEN_CONFIG = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.scoreboard-helper.openConfig",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "category.scoreboard-helper"
    ));
    public static final KeyMapping KEY_BINDING_EXPORT_SCOREBOARD = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.scoreboard-helper.exportScoreboard",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_BACKSLASH,
            "category.scoreboard-helper"
    ));
    public static final KeyMapping KEY_BINDING_EDIT_SCORE = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.scoreboard-helper.editScore",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_PERIOD,
            "category.scoreboard-helper"
    ));

    public static void init() {

    }
}
