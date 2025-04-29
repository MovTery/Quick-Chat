package com.movtery.quick_chat;

import com.movtery.quick_chat.client.ModRegister;
import net.fabricmc.api.ModInitializer;

public class QuickChat implements ModInitializer {

    @Override
    public void onInitialize() {
        // Use Fabric to bootstrap the Common mod.
        CommonClass.init();
        ModRegister.start();
    }
}
