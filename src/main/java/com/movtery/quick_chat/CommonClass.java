package com.movtery.quick_chat;

import com.movtery.quick_chat.platform.Services;

import java.nio.file.Path;

public class CommonClass {
    /**
     * Common initialization
     */
    public static void init() {
        Constants.LOG.info("Loaded {} (Loader: {}, Environment: {})",
                Constants.MOD_NAME, Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());

        Path configPath = Services.PLATFORM.getConfigurationDirectory().resolve(Constants.MOD_ID + ".json");
        Constants.initConfig(configPath.toFile());
    }
}
