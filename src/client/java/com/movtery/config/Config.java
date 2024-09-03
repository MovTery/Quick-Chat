package com.movtery.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.movtery.QuickChatClient;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class Config {
    public static final int[] messageCoolingDurationRange = {1, 15};
    public static final int[] chatQuickMessageButtonWidthRange = {60, 200};
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
                if (options != null) {
                    Set<String> invalidValue = new TreeSet<>();
                    options.message.forEach(v -> {
                        if (v.length() > 256) invalidValue.add(v);
                    });
                    if (!invalidValue.isEmpty()) {
                        options.message.removeAll(invalidValue);
                        save();
                    }
                }
            } catch (IOException e) {
                QuickChatClient.LOGGER.error("Error loading config");
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
            QuickChatClient.LOGGER.error("Error saving config");
        }
    }

    public static class Options {
        public String messageValue = "Hello!";
        public boolean antiFalseContact = false;
        public boolean chatQuickMessageButton = true;
        public int chatQuickMessageButtonWidth = 80;
        public boolean messageCoolingDown = true;
        public int messageCoolingDuration = 10;

        public TreeSet<String> message = new TreeSet<>();
    }
}