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

package top.pigest.scoreboardhelper.util;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    //#if MC >= 1.21.10
    //$$ private static final KeyMapping.Category CATEGORY = KeyMapping.Category.MISC;
    //#endif

    public static final KeyMapping KEY_BINDING_PAGE_UP = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.scoreboard-helper.pageUp",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UP,
            //#if MC >= 1.21.10
            //$$ CATEGORY
            //#else
            "category.scoreboard-helper"
            //#endif
    ));
    public static final KeyMapping KEY_BINDING_PAGE_DOWN = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.scoreboard-helper.pageDown",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_DOWN,
            //#if MC >= 1.21.10
            //$$ CATEGORY
            //#else
            "category.scoreboard-helper"
            //#endif
    ));
    public static final KeyMapping KEY_BINDING_SWITCH_DISPLAY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.scoreboard-helper.switchDisplay",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            //#if MC >= 1.21.10
            //$$ CATEGORY
            //#else
            "category.scoreboard-helper"
            //#endif
    ));
    public static final KeyMapping KEY_BINDING_OPEN_CONFIG = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.scoreboard-helper.openConfig",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            //#if MC >= 1.21.10
            //$$ CATEGORY
            //#else
            "category.scoreboard-helper"
            //#endif
    ));
    public static final KeyMapping KEY_BINDING_EXPORT_SCOREBOARD = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.scoreboard-helper.exportScoreboard",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_BACKSLASH,
            //#if MC >= 1.21.10
            //$$ CATEGORY
            //#else
            "category.scoreboard-helper"
            //#endif
    ));
    public static final KeyMapping KEY_BINDING_EDIT_SCORE = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.scoreboard-helper.editScore",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_PERIOD,
            //#if MC >= 1.21.10
            //$$ CATEGORY
            //#else
            "category.scoreboard-helper"
            //#endif
    ));

    public static void init() {

    }
}
