//? if neoforge {
/*package com.movtery.quick_chat.loader.neoforge;

import com.movtery.quick_chat.platform.services.IPlatformHelper;
import net.neoforged.fml.ModList;
//? if <1.21.9 {
/^import net.neoforged.fml.loading.FMLLoader;
^///?} else {
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
//?}

import java.nio.file.Path;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    //? if <1.21.9 {
    /^@Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public Path getConfigurationDirectory() {
        return FMLLoader.getGamePath().resolve("config");
    }
    ^///?} else {
    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.isProduction();
    }

    @Override
    public Path getConfigurationDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
    //?}
}
*///?}
