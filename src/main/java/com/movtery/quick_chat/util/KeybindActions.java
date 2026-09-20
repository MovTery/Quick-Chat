package com.movtery.quick_chat.util;

import com.movtery.quick_chat.Constants;
import com.movtery.quick_chat.gui.ConfigScreen;
import com.movtery.quick_chat.gui.QuickMessageListScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

/**
 * 三个全局快捷键的定义与按下行为，由各加载器注册并接入 tick 事件
 */
public final class KeybindActions {
    public static final KeyMapping ONE_CLICK = new KeyMapping(
            "quick_chat.keybinding.one_click",
            GLFW.GLFW_KEY_G,
            "quick_chat.name"
    );
    public static final KeyMapping QUICK_MESSAGE = new KeyMapping(
            "quick_chat.keybinding.quick_message",
            GLFW.GLFW_KEY_H,
            "quick_chat.name"
    );
    public static final KeyMapping CONFIG = new KeyMapping(
            "quick_chat.keybinding.config",
            GLFW.GLFW_KEY_J,
            "quick_chat.name"
    );

    private KeybindActions() {
    }

    public static void handleTick() {
        while (ONE_CLICK.consumeClick()) {
            //开启防误触设置之后，将启用双击检测
            if (Constants.getConfig().getOptions().antiFalseContact && QuickChatUtils.notDoubleClick()) break;
            QuickChatUtils.sendMessage(Minecraft.getInstance());
        }
        while (QUICK_MESSAGE.consumeClick()) {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.setScreen(new QuickMessageListScreen(minecraft.screen));
        }
        while (CONFIG.consumeClick()) {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.setScreen(new ConfigScreen(minecraft.screen));
        }
    }
}
