package com.movtery.quick_chat.gui;

import com.movtery.quick_chat.Constants;
import com.movtery.quick_chat.core.ButtonMessageSendMode;
import com.movtery.quick_chat.core.Config;
import com.movtery.quick_chat.gui.widget.TransparentButton;
import com.movtery.quick_chat.util.QuickChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.EditBox;
//? if >=1.21.11 {
import net.minecraft.client.gui.components.ChatComponent;
//?}
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChatQuickMessageButtons {
    private final Minecraft minecraft;
    private final int width, height;
    private final Config.Options options = Constants.getConfig().getOptions();

    public ChatQuickMessageButtons(@NotNull Minecraft minecraft, int width, int height) {
        this.minecraft = minecraft;
        this.width = width;
        this.height = height;
    }

    public ArrayList<TransparentButton> getAllButton(@NotNull ButtonClickListener listener) {
        ArrayList<TransparentButton> buttons = new ArrayList<>();
        if (this.options.chatQuickMessageButton) {
            //? if <1.21.11 {
            int chatWidth = this.minecraft.gui.getChat().getWidth();
            int chatHeight = this.minecraft.gui.getChat().getHeight();
            //?} else {
            int chatWidth = ChatComponent.getWidth(this.minecraft.options.chatWidth().get());
            int chatHeight = ChatComponent.getHeight(this.minecraft.options.chatHeightFocused().get());
            //?}

            addButton(listener, chatWidth, chatHeight, buttons);  //添加按钮
        }
        return buttons;
    }

    private void addButton(@NotNull ButtonClickListener listener, int width, int height, ArrayList<TransparentButton> buttons) {
        int buttonWidth = this.options.getChatButtonWidth();
        int buttonHeight = this.options.getChatButtonHeight();
        ButtonLocation location = new ButtonLocation(width + 18);
        this.options.messageWithComment.forEach(messageObject -> {
            int y = (buttonHeight * location.getColumnIndex()) + 40;
            if (y - 40 > height) {
                location.addX(buttonWidth);
                location.resetColumnIndex();
                y = (buttonHeight * location.getColumnIndex()) + 40;
            } //防止按钮超出聊天栏高度
            if (location.getX() + buttonWidth > this.width) {
                return;
            } //防止超出屏幕宽度

            String message = messageObject.getMessage();
            String comment = messageObject.getComment();

            String showOnButton;
            if (options.displayAsComment && !comment.isEmpty()) {
                showOnButton = comment;
            } else {
                showOnButton = message;
            }

            buttons.add(new TransparentButton.Builder(Component.literal(QuickChatUtils.getAbbreviatedText(showOnButton, this.minecraft, buttonWidth - 6)),
                    button -> listener.onClick((TransparentButton) button, message)).dimensions(location.getX(), this.height - y, buttonWidth, buttonHeight)
                    .tooltip(Tooltip.create(QuickChatUtils.getMessageComponent(message, comment)))
                    .build());
            location.incrementVerticalSequenceIndex();
        });
    }

    /**
     * 处理快捷消息按钮的发送模式行为
     */
    public static void handleSendMode(
            ButtonMessageSendMode mode,
            Minecraft minecraft,
            TransparentButton button,
            EditBox input,
            String message,
            Runnable closeScreen
    ) {
        switch (mode) {
            case CLICK_TO_SEND -> {
                if (Constants.getConfig().getOptions().antiFalseContact && button.notDoubleClick()) {
                    return;
                }
                if (minecraft.player != null && !minecraft.player.isSleeping()) {
                    closeScreen.run();
                }
                QuickChatUtils.sendMessage(minecraft, message);
            }
            case PASTE_TO_INPUT -> input.setValue(input.getValue() + message);
            case REPLACE_INPUT -> input.setValue(message);
        }
    }

    private static class ButtonLocation {
        private int columnIndex, x;

        public ButtonLocation(int x) {
            this.columnIndex = 1;
            this.x = x;
        }

        public int getColumnIndex() {
            return columnIndex;
        }

        public void resetColumnIndex() {
            this.columnIndex = 1;
        }

        public void incrementVerticalSequenceIndex() {
            this.columnIndex++;
        }

        public int getX() {
            return x;
        }

        public void addX(int x) {
            this.x += x + 6;
        }
    }

    public interface ButtonClickListener {
        void onClick(TransparentButton button, String message);
    }
}
