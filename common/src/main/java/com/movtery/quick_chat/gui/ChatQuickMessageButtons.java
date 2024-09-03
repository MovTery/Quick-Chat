package com.movtery.quick_chat.gui;

import com.movtery.quick_chat.config.Config;
import com.movtery.quick_chat.gui.widget.TransparentButton;
import com.movtery.quick_chat.util.QuickChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.movtery.quick_chat.QuickChat.getConfig;
import static com.movtery.quick_chat.config.Config.chatQuickMessageButtonWidthRange;

public class ChatQuickMessageButtons {
    private final Minecraft minecraft;
    private final int width, height;
    private final Config.Options options = getConfig().getOptions();

    public ChatQuickMessageButtons(@NotNull Minecraft minecraft, int width, int height) {
        this.minecraft = minecraft;
        this.width = width;
        this.height = height;
    }

    public ArrayList<TransparentButton> getAllButton(@NotNull ButtonClickListener listener) {
        ArrayList<TransparentButton> buttons = new ArrayList<>();
        if (this.options.chatQuickMessageButton) {
            int chatWidth = this.minecraft.gui.getChat().getWidth();
            int chatHeight = this.minecraft.gui.getChat().getHeight();

            addButton(listener, chatWidth, chatHeight, buttons);  //添加按钮
        }
        return buttons;
    }

    private void addButton(@NotNull ButtonClickListener listener, int width, int height, ArrayList<TransparentButton> buttons) {
        int[] widthRange = chatQuickMessageButtonWidthRange;
        int buttonWidth = this.options.chatQuickMessageButtonWidth > widthRange[1] ? widthRange[1] : Math.max(this.options.chatQuickMessageButtonWidth, widthRange[0]);
        ButtonLocation location = new ButtonLocation(width + 18);
        this.options.message.forEach(message -> {
            int y = (20 * location.getColumnIndex()) + 40;
            if (y - 40 > height) {
                location.addX(buttonWidth);
                location.resetColumnIndex();
                y = (20 * location.getColumnIndex()) + 40;
            } //防止按钮超出聊天栏高度
            if (location.getX() + buttonWidth > this.width) {
                return;
            } //防止超出屏幕宽度

            buttons.add(new TransparentButton.Builder(Component.literal(QuickChatUtils.getAbbreviatedText(message, this.minecraft, buttonWidth - 6)),
                    button -> listener.onClick((TransparentButton) button, message)).dimensions(location.getX(), this.height - y, buttonWidth, 20)
                    .tooltip(Tooltip.create(Component.translatable("quick_chat.gui.message_list.send")
                            .append("\n")
                            .append(message)))
                    .build());
            location.incrementVerticalSequenceIndex();
        });
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
