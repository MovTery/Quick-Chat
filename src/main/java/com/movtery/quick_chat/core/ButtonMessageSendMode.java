package com.movtery.quick_chat.core;

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
}
