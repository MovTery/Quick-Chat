package com.movtery.quick_chat.fabric.client;

import com.movtery.quick_chat.gui.ConfigScreen;
import com.movtery.quick_chat.gui.QuickMessageListScreen;
import com.movtery.quick_chat.util.QuickChatUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

import static com.movtery.quick_chat.QuickChat.getConfig;

public class ModRegister {
    public static void start() {
        KeyMapping oneClick = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "quick_chat.keybinding.one_click",
                GLFW.GLFW_KEY_G,
                "quick_chat.name"
        ));

        KeyMapping quickMessage = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "quick_chat.keybinding.quick_message",
                GLFW.GLFW_KEY_H,
                "quick_chat.name"
        ));

        KeyMapping config = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "quick_chat.keybinding.config",
                GLFW.GLFW_KEY_J,
                "quick_chat.name"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (oneClick.consumeClick()) {
                //开启防误触设置之后，将启用双击检测
                if (getConfig().getOptions().antiFalseContact) {
                    if (QuickChatUtils.notDoubleClick()) break;
                }
                if (Objects.isNull(client)) break;

                QuickChatUtils.sendMessage(client);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> openScreenOnKeyPress(quickMessage, minecraft, new QuickMessageListScreen(minecraft.screen)));
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> openScreenOnKeyPress(config, minecraft, new ConfigScreen(minecraft.screen)));
    }

    private static void openScreenOnKeyPress(KeyMapping key, Minecraft minecraft, Screen screen) {
        while (key.consumeClick()) {
            minecraft.setScreen(screen);
        }
    }
}
