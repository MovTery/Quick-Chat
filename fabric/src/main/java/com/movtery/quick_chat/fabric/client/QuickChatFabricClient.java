package com.movtery.quick_chat.fabric.client;

import com.movtery.quick_chat.QuickChat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

public final class QuickChatFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        String fileName = (QuickChat.MOD_ID + ".json");
        File configFolder = FabricLoader.getInstance().getConfigDir().toFile();
        File configFile = new File(configFolder, fileName);
        QuickChat.init(configFile);
        ModRegister.start();
    }
}
