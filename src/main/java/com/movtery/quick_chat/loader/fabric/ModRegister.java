//? if fabric {
package com.movtery.quick_chat.loader.fabric;

import com.movtery.quick_chat.util.KeybindActions;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class ModRegister {
    public static void start() {
        KeyBindingHelper.registerKeyBinding(KeybindActions.ONE_CLICK);
        KeyBindingHelper.registerKeyBinding(KeybindActions.QUICK_MESSAGE);
        KeyBindingHelper.registerKeyBinding(KeybindActions.CONFIG);

        ClientTickEvents.END_CLIENT_TICK.register(client -> KeybindActions.handleTick());
    }
}
//?}
