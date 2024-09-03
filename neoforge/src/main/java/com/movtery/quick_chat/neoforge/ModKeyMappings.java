package com.movtery.quick_chat.neoforge;

import net.minecraft.client.KeyMapping;
import net.neoforged.jarjar.nio.util.Lazy;
import org.lwjgl.glfw.GLFW;

public class ModKeyMappings {
    public static final Lazy<KeyMapping> ONE_CLICK = Lazy.of(() -> new KeyMapping(
            "quick_chat.keybinding.one_click",
            GLFW.GLFW_KEY_G,
            "quick_chat.name"
    ));
    public static final Lazy<KeyMapping> QUICK_MESSAGE = Lazy.of(() -> new KeyMapping(
            "quick_chat.keybinding.quick_message",
            GLFW.GLFW_KEY_H,
            "quick_chat.name"
    ));
    public static final Lazy<KeyMapping> CONFIG = Lazy.of(() -> new KeyMapping(
            "quick_chat.keybinding.config",
            GLFW.GLFW_KEY_J,
            "quick_chat.name"
    ));
}
