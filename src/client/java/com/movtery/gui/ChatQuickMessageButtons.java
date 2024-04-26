package com.movtery.gui;

import com.movtery.config.Config;
import com.movtery.gui.widget.TransparentButton;
import com.movtery.util.QuickChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.movtery.util.QuickChatUtils.*;

public class ChatQuickMessageButtons {
    private final MinecraftClient client;
    private final ClientPlayerEntity player;
    private final int width, height;
    private final Screen chatScreen;
    private final Config.Options options = getConfig().getOptions();

    public ChatQuickMessageButtons(@NotNull MinecraftClient client, @NotNull ClientPlayerEntity player, @NotNull Screen chatScreen, int width, int height) {
        this.client = client;
        this.player = player;
        this.width = width;
        this.height = height;
        this.chatScreen = chatScreen;
    }

    public ArrayList<TransparentButton> getAllButton() {
        ArrayList<TransparentButton> buttons = new ArrayList<>();
        if (this.options.chatQuickMessageButton) {
            int chatWidth = this.client.inGameHud.getChatHud().getWidth();
            int chatHeight = this.client.inGameHud.getChatHud().getHeight();

            int buttonWidth = addButton(chatWidth, chatHeight, buttons);  //添加按钮

            TransparentButton messageListButton = new TransparentButton(chatWidth + 18, this.height - 40,
                    Math.min(buttonWidth, this.width - chatWidth - 18 - 2), 20,
                    Text.translatable("quick_chat.gui.message_list.title"),
                    button1 -> this.client.setScreen(new QuickMessageListScreen(null)));

            buttons.add(messageListButton);
        }
        return buttons;
    }

    private int addButton(int width, int height, ArrayList<TransparentButton> buttons) {
        int buttonWidth = this.options.chatQuickMessageButtonWidth;
        int[] data = {1, width + 18, buttonWidth};
        this.options.message.forEach(s -> {
            int y = ((20 + 2) * data[0]) + 40;
            if (y - 40 > height) {
                data[1] += buttonWidth + 2;
                data[2] += buttonWidth + 2;
                data[0] = 1;
                y = ((20 + 2) * data[0]) + 40;
            } //防止按钮超出聊天栏高度
            if (data[1] + buttonWidth > this.width) {
                data[2] -= data[2] > buttonWidth + 2 ? buttonWidth + 2 : 0;
                return;
            } //防止超出屏幕宽度

            buttons.add(new TransparentButton.Builder(Text.literal(getAbbreviatedText(s, this.client, buttonWidth - 6)), button -> {
                if (options.antiFalseContact) {
                    if (isDoubleClick()) return;
                }
                this.chatScreen.close();
                QuickChatUtils.sendMessage(this.player, s);
            }).dimensions(data[1], this.height - y, buttonWidth, 20)
                    .tooltip(Tooltip.of(Text.translatable("quick_chat.gui.message_list.send")
                            .append("\n")
                            .append(s)))
                    .build());
            data[0]++;
        });
        return data[2];
    }
}