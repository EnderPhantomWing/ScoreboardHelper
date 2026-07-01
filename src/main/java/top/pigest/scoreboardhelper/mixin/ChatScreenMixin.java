package top.pigest.scoreboardhelper.mixin;

import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.pigest.scoreboardhelper.config.ScoreboardHelperConfig;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {

    protected ChatScreenMixin(Component title) {
        super(title);
    }

    @Redirect(method = "handleChatInput",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;sendChat(Ljava/lang/String;)V"))
    private void injected1(ClientPacketListener instance, String content) {
        if(ScoreboardHelperConfig.INSTANCE.defaultTeamChat.getValue()) {
            if(content.startsWith("#")) {
                if(content.length() == 1) {
                    instance.sendChat(content);
                } else {
                    instance.sendChat(content.substring(1));
                }
            } else {
                instance.sendCommand("teammsg "+ content);
            }
        } else {
            instance.sendChat(content);
        }
    }


}
