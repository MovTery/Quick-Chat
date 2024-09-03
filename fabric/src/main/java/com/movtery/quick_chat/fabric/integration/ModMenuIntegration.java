package com.movtery.quick_chat.fabric.integration;

import com.movtery.quick_chat.gui.ConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screens.Screen;

public class ModMenuIntegration implements ModMenuApi {
    public ModMenuIntegration() {
    }

    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (ConfigScreenFactory<Screen>) ConfigScreen::new;
    }
}
