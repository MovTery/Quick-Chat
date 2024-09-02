package com.movtery;

import com.movtery.gui.ConfigScreen;
import com.movtery.gui.QuickMessageListScreen;
import com.movtery.util.QuickChatUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

import static com.movtery.QuickChatClient.*;

public class ModRegister {
    public static void start() {
        getConfig(); //加载配置文件

        KeyBinding oneClick = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "quick_chat.keybinding.one_click",
                GLFW.GLFW_KEY_G,
                "quick_chat.name"
        ));

        KeyBinding quickMessage = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "quick_chat.keybinding.quick_message",
                GLFW.GLFW_KEY_H,
                "quick_chat.name"
        ));

        KeyBinding config = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "quick_chat.keybinding.config",
                GLFW.GLFW_KEY_J,
                "quick_chat.name"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (oneClick.wasPressed()) {
                //开启防误触设置之后，将启用双击检测
                if (getConfig().getOptions().antiFalseContact) {
                    if (QuickChatUtils.notDoubleClick()) break;
                }
                if (Objects.isNull(client)) break;

                QuickChatUtils.sendMessage(client);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> openScreenOnKeyPress(quickMessage, client, new QuickMessageListScreen(client.currentScreen)));
        ClientTickEvents.END_CLIENT_TICK.register(client -> openScreenOnKeyPress(config, client, new ConfigScreen(client.currentScreen)));
    }

    private static void openScreenOnKeyPress(KeyBinding key, MinecraftClient minecraftClient, Screen screen) {
        while (key.wasPressed()) {
            minecraftClient.setScreen(screen);
        }
    }
}
