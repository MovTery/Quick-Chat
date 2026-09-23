package com.movtery.quick_chat.util;

import com.movtery.quick_chat.Constants;
import com.movtery.quick_chat.gui.ConfigScreen;
import com.movtery.quick_chat.gui.QuickMessageListScreen;
import com.movtery.quick_chat.gui.WheelScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
//? if <26.2 {
/*import org.lwjgl.glfw.GLFW;
*///?} else {
import com.mojang.blaze3d.platform.InputConstants;
//?}
//? if >=1.21.11 {
import net.minecraft.resources.Identifier;
//?} else {
/*import net.minecraft.resources.ResourceLocation;
*///?}

/**
 * 三个全局快捷键的定义与按下行为，由各加载器注册并接入 tick 事件
 */
public final class KeybindActions {
    //? if <26.2 {
    /*private static final int KEY_WHEEL = GLFW.GLFW_KEY_G;
    private static final int KEY_QUICK_MESSAGE = GLFW.GLFW_KEY_H;
    private static final int KEY_CONFIG = GLFW.GLFW_KEY_J;
    *///?} else {
    private static final int KEY_WHEEL = InputConstants.KEY_G;
    private static final int KEY_QUICK_MESSAGE = InputConstants.KEY_H;
    private static final int KEY_CONFIG = InputConstants.KEY_J;
    //?}

    //? if <1.21.9 {
    /*public static final KeyMapping WHEEL = key("quick_chat.keybinding.wheel", KEY_WHEEL);
    public static final KeyMapping QUICK_MESSAGE = key("quick_chat.keybinding.quick_message", KEY_QUICK_MESSAGE);
    public static final KeyMapping CONFIG = key("quick_chat.keybinding.config", KEY_CONFIG);

    private KeybindActions() {
    }

    private static KeyMapping key(String name, int defaultKey) {
        return new KeyMapping(name, defaultKey, "quick_chat.name");
    }
    *///?} else {
    //? if fabric {
    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(categoryId());
    //?} else {
    /*public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(categoryId());
    *///?}

    //? if <1.21.11 {
    /*private static ResourceLocation categoryId() {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "main");
    }
    *///?} else {
    private static Identifier categoryId() {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "main");
    }
    //?}

    public static final KeyMapping WHEEL = key("quick_chat.keybinding.wheel", KEY_WHEEL);
    public static final KeyMapping QUICK_MESSAGE = key("quick_chat.keybinding.quick_message", KEY_QUICK_MESSAGE);
    public static final KeyMapping CONFIG = key("quick_chat.keybinding.config", KEY_CONFIG);

    private KeybindActions() {
    }

    private static KeyMapping key(String name, int defaultKey) {
        return new KeyMapping(name, defaultKey, CATEGORY);
    }
    //?}

    public static void handleTick() {
        while (WHEEL.consumeClick()) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
                QuickChatUtils.openScreen(minecraft, new WheelScreen());
            }
        }
        while (QUICK_MESSAGE.consumeClick()) {
            Minecraft minecraft = Minecraft.getInstance();
            //? if <26.2 {
            /*QuickChatUtils.openScreen(minecraft, new QuickMessageListScreen(minecraft.screen));
            *///?} else {
            QuickChatUtils.openScreen(minecraft, new QuickMessageListScreen(minecraft.gui.screen()));
            //?}
        }
        while (CONFIG.consumeClick()) {
            Minecraft minecraft = Minecraft.getInstance();
            //? if <26.2 {
            /*QuickChatUtils.openScreen(minecraft, new ConfigScreen(minecraft.screen));
            *///?} else {
            QuickChatUtils.openScreen(minecraft, new ConfigScreen(minecraft.gui.screen()));
            //?}
        }
    }
}
