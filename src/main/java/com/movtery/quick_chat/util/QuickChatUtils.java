package com.movtery.quick_chat.util;

import com.movtery.quick_chat.Constants;
import com.movtery.quick_chat.core.Config;
import com.movtery.quick_chat.core.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;
//? if <26.2 {
/*import org.lwjgl.glfw.GLFW;
*///?} else {
import com.mojang.blaze3d.platform.InputConstants;
//?}

import java.math.BigDecimal;
import java.util.Date;

public final class QuickChatUtils {
    private QuickChatUtils() {
    }

    public static void openScreen(@NotNull Minecraft minecraft, @NotNull Screen screen) {
        //? if <26.2 {
        /*minecraft.setScreen(screen);
        *///?} else {
        minecraft.setScreenAndShow(screen);
        //?}
    }

    public static String getAbbreviatedText(String message, @NotNull Minecraft minecraft, int width) {
        if (!(minecraft.font.width(message) > width)) return message;
        String text = minecraft.font.plainSubstrByWidth(message, width);
        return text.substring(0, text.length() - 3) + "...";
    }

    public static boolean isEnter(int keyCode) {
        //? if <26.2 {
        /*return keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER;
        *///?} else {
        return keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_NUMPADENTER;
        //?}
    }

    public static void sendMessage(@NotNull Minecraft minecraft, String message) {
        LocalPlayer player = minecraft.player;
        if (player == null) return;

        Date date = new Date();
        long timeNum = date.getTime(); //获取当前时间
        long lastTime = LastMessage.getInstance().getLastTime(); //获取上一次发送的时间

        Config.Options options = Constants.getConfig().getOptions();
        double duration = (double) options.getCoolingDuration();

        //检查上一次发送消息的时间，如果间隔时间不符合要求则提示过于频繁，不再发送消息
        long differ = timeNum - lastTime; //计算时间差
        if (!options.messageCoolingDown || (lastTime == 0 || differ > 1000 * duration)) {
            if (differ > 500) {
                //? if <26.2 {
                /*minecraft.gui.getChat().addRecentChat(message);
                *///?} else {
                minecraft.gui.hud.getChat().addRecentChat(message);
                //?}

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
            //? if <26.1 {
            /*player.displayClientMessage(Component.translatable("quick_chat.in_game.too_often").append(String.format(" %.2fs", t)), true);
            *///?}
            //? if 26.1 {
            /*minecraft.gui.getChat().addClientSystemMessage(Component.translatable("quick_chat.in_game.too_often").append(String.format(" %.2fs", t)));
            *///?}
            //? if >=26.2 {
            minecraft.gui.hud.getChat().addClientSystemMessage(Component.translatable("quick_chat.in_game.too_often").append(String.format(" %.2fs", t)));
            //?}
        }
    }

    public static MutableComponent getMessageComponent(Message messageObject) {
        return getMessageComponent(messageObject.getMessage(), messageObject.getComment());
    }

    public static MutableComponent getMessageComponent(String message, String comment) {
        MutableComponent component = Component.literal(message).append("\n\n");
        if (!comment.isEmpty()) {
            component.append(Component.translatable("quick_chat.config.comment.tooltip"))
                    .append("\n")
                    .append(comment);
        } else {
            component.append(Component.translatable("quick_chat.config.no_comment.tooltip"));
        }
        return component;
    }
}
