package com.movtery.quick_chat.config;

import com.movtery.quick_chat.QuickChat;
import com.movtery.quick_chat.gui.widget.TransparentButton;
import com.movtery.quick_chat.util.QuickChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;

public enum ButtonMessageSendMode {
    CLICK_TO_SEND("click_to_send"),
    PASTE_TO_INPUT("paste_to_input"),
    REPLACE_INPUT("replace_input");

    private final String translateKey;

    ButtonMessageSendMode(String translateKey) {
        this.translateKey = translateKey;
    }

    public String getTranslateKey() {
        return "quick_chat.config.chat_button.send_mode." + translateKey;
    }

    public String getTooltipTranslateKey() {
        return getTranslateKey() + ".desc";
    }

    public void handleClickEvent(
            Minecraft minecraft,
            TransparentButton button,
            EditBox input,
            String message,
            FunctionToCloseScreen closeScreen
    ) {
        switch (this) {
            case CLICK_TO_SEND -> {
                if (QuickChat.getConfig().getOptions().antiFalseContact && button.notDoubleClick()) {
                    return;
                }
                if (minecraft.player != null && !minecraft.player.isSleeping()) {
                    closeScreen.onClose();
                }
                QuickChatUtils.sendMessage(minecraft, message);
            }
            case PASTE_TO_INPUT -> input.setValue(input.getValue() + message);
            case REPLACE_INPUT -> input.setValue(message);
        }
    }

    public interface FunctionToCloseScreen {
        void onClose();
    }
}
