//? if neoforge {
package com.movtery.quick_chat.loader.neoforge;

import com.movtery.quick_chat.gui.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
//? if <1.20.5 {
import net.neoforged.neoforge.client.ConfigScreenHandler;
//?} else {
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
//?}

public class RegisterModsPage {
    public static void registerModsPage() {
        //? if <1.20.5 {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> new ConfigScreen(screen)));
        //?}
        //? if 1.20.6 {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> new IConfigScreenFactory() {
            @Override
            public Screen createScreen(Minecraft minecraft, Screen screen) {
                return new ConfigScreen(screen);
            }
        });
        //?}
        //? if >=1.21 {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> new IConfigScreenFactory() {
            @Override
            public Screen createScreen(ModContainer modContainer, Screen screen) {
                return new ConfigScreen(screen);
            }
        });
        //?}
    }
}
//?}
