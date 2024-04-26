package com.movtery.mixin.client;

import com.movtery.gui.ChatQuickMessageButtons;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen {

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void init(CallbackInfo info) {
        if (this.client != null && this.client.player != null) {
            ChatQuickMessageButtons buttons = new ChatQuickMessageButtons(this.client, this.client.player, this, this.width, this.height);
            buttons.getAllButton().forEach(this::addDrawableChild);
        }
    }
}