package com.movtery.quick_chat.util;

import com.movtery.quick_chat.Constants;
import com.movtery.quick_chat.gui.ConfigScreen;
import com.movtery.quick_chat.gui.QuickMessageListScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
//? if <26.2 {
import org.lwjgl.glfw.GLFW;
//?} else {
import com.mojang.blaze3d.platform.InputConstants;
//?}

/**
 * 三个全局快捷键的定义与按下行为，由各加载器注册并接入 tick 事件
 */
public final class KeybindActions {
    //? if <26.2 {
    private static final int KEY_ONE_CLICK = GLFW.GLFW_KEY_G;
    private static final int KEY_QUICK_MESSAGE = GLFW.GLFW_KEY_H;
    private static final int KEY_CONFIG = GLFW.GLFW_KEY_J;
    //?} else {
    private static final int KEY_ONE_CLICK = InputConstants.KEY_G;
    private static final int KEY_QUICK_MESSAGE = InputConstants.KEY_H;
    private static final int KEY_CONFIG = InputConstants.KEY_J;
    //?}

    public static final KeyMapping ONE_CLICK = key("quick_chat.keybinding.one_click", KEY_ONE_CLICK);
    public static final KeyMapping QUICK_MESSAGE = key("quick_chat.keybinding.quick_message", KEY_QUICK_MESSAGE);
    public static final KeyMapping CONFIG = key("quick_chat.keybinding.config", KEY_CONFIG);

    private KeybindActions() {
    }

    //? if <1.21.11 {
    private static KeyMapping key(String name, int defaultKey) {
        return new KeyMapping(name, defaultKey, "quick_chat.name");
    }
    //?} else {
    private static KeyMapping key(String name, int defaultKey) {
        return new KeyMapping(name, defaultKey, KeyMapping.Category.MISC);
    }
    //?}

    public static void handleTick() {
        while (ONE_CLICK.consumeClick()) {
            //开启防误触设置之后，将启用双击检测
            if (Constants.getConfig().getOptions().antiFalseContact && QuickChatUtils.notDoubleClick()) break;
            QuickChatUtils.sendMessage(Minecraft.getInstance());
        }
        while (QUICK_MESSAGE.consumeClick()) {
            Minecraft minecraft = Minecraft.getInstance();
            //? if <26.2 {
            QuickChatUtils.openScreen(minecraft, new QuickMessageListScreen(minecraft.screen));
            //?} else {
            QuickChatUtils.openScreen(minecraft, new QuickMessageListScreen(minecraft.gui.screen()));
            //?}
        }
        while (CONFIG.consumeClick()) {
            Minecraft minecraft = Minecraft.getInstance();
            //? if <26.2 {
            QuickChatUtils.openScreen(minecraft, new ConfigScreen(minecraft.screen));
            //?} else {
            QuickChatUtils.openScreen(minecraft, new ConfigScreen(minecraft.gui.screen()));
            //?}
        }
    }
}
