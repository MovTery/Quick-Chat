package com.movtery.util;

import com.movtery.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.math.BigDecimal;
import java.util.Date;

import static com.movtery.QuickChatClient.getConfig;
import static com.movtery.config.Config.messageCoolingDurationRange;

public class QuickChatUtils {
    public static boolean notDoubleClick() {
        LastMessage instance = LastMessage.getInstance();
        long clickTime = Util.getMeasuringTimeMs();
        //点击即进行判断，如果前后两次点击时间相差不超过0.25秒，那么表示这是一次双击
        boolean isDoubleClick = clickTime - instance.getLastClick() < 250L;
        instance.setLastClick(clickTime);

        return !isDoubleClick;
    }

    public static String getAbbreviatedText(String message, @NotNull MinecraftClient client, int width) {
        if (!(client.textRenderer.getWidth(message) > width)) return message;
        String text = client.textRenderer.trimToWidth(message, width);
        return text.substring(0, text.length() - 3) + "...";
    }

    public static boolean isEnter(int keyCode) {
        return keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER;
    }

    public static void sendMessage(@NotNull MinecraftClient client) {
        Config.Options options = getConfig().getOptions();

        String message = options.messageValue;
        sendMessage(client, message);
    }

    public static void sendMessage(@NotNull MinecraftClient client, String message) {
        ClientPlayerEntity player = client.player;
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
                client.inGameHud.getChatHud().addToMessageHistory(message);

                if (!message.startsWith("/")) {
                    player.networkHandler.sendChatMessage(message);
                } else {
                    player.networkHandler.sendCommand(message.substring(1));
                }
                LastMessage.getInstance().setLastTime(timeNum);
                LastMessage.getInstance().setLastMessage(message);
            }
        } else {
            BigDecimal bigDecimal = BigDecimal.valueOf(duration);
            BigDecimal t = bigDecimal.subtract(BigDecimal.valueOf(differ / 1000.0));
            player.sendMessage(Text.translatable("quick_chat.in_game.too_often").append(String.format(" %.2fs", t)), true);
        }
    }
}
