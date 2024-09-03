package com.movtery.quick_chat.mixin;


import com.movtery.quick_chat.QuickChat;
import com.movtery.quick_chat.gui.ChatQuickMessageButtons;
import com.movtery.quick_chat.util.QuickChatUtils;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen {
    protected ChatScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        if (this.minecraft != null && this.minecraft.player != null) {
            ChatQuickMessageButtons buttons = new ChatQuickMessageButtons(this.minecraft, this.width, this.height);
            buttons.getAllButton((button, message) -> {
                if (QuickChat.getConfig().getOptions().antiFalseContact && button.notDoubleClick()) {
                    return;
                }
                if (!this.minecraft.player.isSleeping()) this.onClose();
                QuickChatUtils.sendMessage(this.minecraft, message);
            }).forEach(this::addRenderableWidget);
        }
    }
}
