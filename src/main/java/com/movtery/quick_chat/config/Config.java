package com.movtery.quick_chat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.movtery.quick_chat.QuickChat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.TreeSet;

public class Config {
    public static final double[] messageCoolingDurationRange = {1.0, 15.0};
    private final File file;
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private Options options = null;

    public Config(File file) {
        this.file = file;
    }

    public Options getOptions() {
        return options;
    }

    public void load() {
        if (file.exists()) {
            try {
                options = GSON.fromJson(Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8), Options.class);
            } catch (IOException e) {
                QuickChat.LOGGER.error("Error loading config");
            }

        }
        if (options == null) {
            options = new Options();
            save();
        }
    }

    public void save() {
        try {
            Files.write(file.toPath(), Collections.singleton(GSON.toJson(options)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            QuickChat.LOGGER.error("Error saving config");
        }
    }

    public static class Options {
        public String messageValue = "Hello!";
        public boolean antiFalseContact = false;
        public boolean messageCoolingDown = true;
        public int messageCoolingDuration = 10;

        public TreeSet<String> message = new TreeSet<>();
    }
}