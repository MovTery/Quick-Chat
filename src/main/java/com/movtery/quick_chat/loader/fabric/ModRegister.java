//? if fabric {
package com.movtery.quick_chat.loader.fabric;

import com.movtery.quick_chat.util.KeybindActions;
//? if <26.1 {
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
//?} else {
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
//?}

public class ModRegister {
    public static void start() {
        //? if <26.1 {
        KeyBindingHelper.registerKeyBinding(KeybindActions.ONE_CLICK);
        KeyBindingHelper.registerKeyBinding(KeybindActions.QUICK_MESSAGE);
        KeyBindingHelper.registerKeyBinding(KeybindActions.CONFIG);
        //?} else {
        KeyMappingHelper.registerKeyMapping(KeybindActions.ONE_CLICK);
        KeyMappingHelper.registerKeyMapping(KeybindActions.QUICK_MESSAGE);
        KeyMappingHelper.registerKeyMapping(KeybindActions.CONFIG);
        //?}

        ClientTickEvents.END_CLIENT_TICK.register(client -> KeybindActions.handleTick());
    }
}
//?}
