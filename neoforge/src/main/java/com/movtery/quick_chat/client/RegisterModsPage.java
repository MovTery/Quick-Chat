package com.movtery.quick_chat.client;

import com.movtery.quick_chat.gui.ConfigScreen;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.NotNull;

public class RegisterModsPage {
    public static void registerModsPage() {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> new IConfigScreenFactory() {
            @Override
            public @NotNull Screen createScreen(@NotNull ModContainer modContainer, @NotNull Screen screen) {
                return new ConfigScreen(screen);
            }
        });
    }
}