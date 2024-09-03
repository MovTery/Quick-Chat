package com.movtery.quick_chat.util;

import com.movtery.quick_chat.config.Config;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.math.BigDecimal;
import java.util.Date;

import static com.movtery.quick_chat.QuickChat.getConfig;
import static com.movtery.quick_chat.config.Config.messageCoolingDurationRange;

public class QuickChatUtils {
    public static boolean notDoubleClick() {
        LastMessage instance = LastMessage.getInstance();
        long clickTime = Util.getMillis();
        //点击即进行判断，如果前后两次点击时间相差不超过0.25秒，那么表示这是一次双击
        boolean isDoubleClick = clickTime - instance.getLastClick() < 250L;
        instance.setLastClick(clickTime);

        return !isDoubleClick;
    }

    public static String getAbbreviatedText(String message, @NotNull Minecraft minecraft, int width) {
        if (!(minecraft.font.width(message) > width)) return message;
        String text = minecraft.font.plainSubstrByWidth(message, width);
        return text.substring(0, text.length() - 3) + "...";
    }

    public static boolean isEnter(int keyCode) {
        return keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER;
    }

    public static void sendMessage(@NotNull Minecraft minecraft) {
        Config.Options options = getConfig().getOptions();

        String message = options.messageValue;
        sendMessage(minecraft, message);
    }

    public static void sendMessage(@NotNull Minecraft minecraft, String message) {
        LocalPlayer player = minecraft.player;
        if (player == null) return;

        Date date = new Date();
        long timeNum = date.getTime(); //获取当前时间
        long lastTime = LastMessage.getInstance().getLastTime(); //获取上一次发送的时间

        Config.Options options = getConfig().getOptions();
        double duration = Math.abs(options.messageCoolingDuration);
        duration = duration > messageCoolingDurationRange[1] ? messageCoolingDurationRange[1] : Math.max(duration, messageCoolingDurationRange[0]);

        //检查上一次发送消息的时间，如果间隔时间不符合要求则提示过于频繁，不再发送消息
        long differ = timeNum - lastTime; //计算时间差
        if (!options.messageCoolingDown || (lastTime == 0 || differ > 1000 * duration)) {
            if (differ > 500) {
                minecraft.gui.getChat().addRecentChat(message);

                if (!message.startsWith("/")) {
                    player.connection.sendChat(message);
                } else {
                    player.connection.sendCommand(message.substring(1));
                }
                LastMessage.getInstance().setLastTime(timeNum);
                LastMessage.getInstance().setLastMessage(message);
            }
        } else {
            BigDecimal bigDecimal = BigDecimal.valueOf(duration);
            BigDecimal t = bigDecimal.subtract(BigDecimal.valueOf(differ / 1000.0));
            player.displayClientMessage(Component.translatable("quick_chat.in_game.too_often").append(String.format(" %.2fs", t)), true);
        }
    }
}
