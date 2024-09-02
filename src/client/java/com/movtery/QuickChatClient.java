package com.movtery;

import com.movtery.config.Config;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class QuickChatClient implements ClientModInitializer {
    public static final String MODID = "quick_chat";
    public static final Logger LOGGER = LoggerFactory.getLogger("Quick Chat");
    private static Config config = null;

    public static Config getConfig() {
        if (config == null) loadConfig();
        return config;
    }

    private static void loadConfig() {
        String fileName = (MODID + ".json");
        Path configPath = FabricLoader.getInstance().getConfigDir();
        File configFile = new File(configPath + "\\" + fileName);
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

    @Override
    public void onInitializeClient() {
        ModRegister.start();
    }
}
