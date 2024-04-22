package com.movtery.integration;

import com.movtery.QuickChatClient;
import com.movtery.config.YACLConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenuIntegration implements ModMenuApi {
    public ModMenuIntegration() {
    }

    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (!FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3")) {
            QuickChatClient.LOGGER.info("The YetAnotherConfigLib Mod is not installed, and in-game configuration changes will not be supported!");
            return null;
        }
        YACLConfig configScreen = new YACLConfig();
        return configScreen::configScreen;
    }
}
