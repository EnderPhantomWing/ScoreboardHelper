package top.pigest.scoreboardhelper.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;

@Environment(value = EnvType.CLIENT)
public record SidebarEntry(Component name, Component score, int scoreWidth) {
}
