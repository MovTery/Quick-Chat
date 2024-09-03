package com.movtery.quick_chat;

import com.movtery.quick_chat.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public final class QuickChat {
    public static final String MOD_ID = "quick_chat";
    public static final Logger LOGGER = LoggerFactory.getLogger("Quick Chat");
    private static Config config = null;

    public static Config getConfig() {
        return config;
    }

    public static void init(File configFile) {
        if (!configFile.exists()) {
            boolean succeed;
            try {
                succeed = configFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (succeed) LOGGER.info("Created a new configuration file: {}", configFile.getPath());
        }
        config = new Config(configFile);
        config.load();
    }
}
