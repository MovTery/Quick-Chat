package com.movtery.quick_chat.mixin.client;

import com.movtery.quick_chat.QuickChat;
import com.movtery.quick_chat.gui.ChatQuickMessageButtons;
import com.movtery.quick_chat.gui.widget.TransparentButton;
import com.movtery.quick_chat.util.GUIShenanigans;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen {
    @Shadow protected EditBox input;

    private ChatScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        if (this.minecraft != null && this.minecraft.player != null) {
            ChatQuickMessageButtons buttons = new ChatQuickMessageButtons(this.minecraft, this.width, this.height);
            ArrayList<TransparentButton> allButton = buttons.getAllButton((button, message) ->
                    QuickChat.getConfig().getOptions().buttonMessageSendMode.handleClickEvent(
                            minecraft, button,
                            this.input, message,
                            this::onClose
                    )
            );
            if (!allButton.isEmpty()) {
                for (TransparentButton transparentButton : allButton) {
                    this.addRenderableWidget(transparentButton);
                }
            }
        }
    }

    @Override
    protected void changeFocus(ComponentPath componentPath) {
        if (GUIShenanigans.getLeaf(componentPath).component() instanceof EditBox) {
            super.changeFocus(componentPath);
        }
    }

    @Override
    public void setFocused(@Nullable GuiEventListener guiEventListener) {
        if (guiEventListener instanceof EditBox) {
            super.setFocused(guiEventListener);
        }
    }
}
