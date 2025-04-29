package com.movtery.quick_chat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.movtery.quick_chat.Constants;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

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
                    if (options.messageValue != null) {
                        String messageValue = options.messageValue;
                        if (messageValue.length() > 256) {
                            options.messageValue = messageValue.substring(0, 256);
                        }
                    } else options.messageValue = "Hello!";

                    if (!options.message.isEmpty()) {
                        for (String item : options.message) {
                            options.messageWithComment.put(item, "");
                        }
                        options.message.clear();
                    }

                    options.messageWithComment = options.messageWithComment
                            .entrySet()
                            .stream()
                            .collect(
                                    Collectors.toMap(
                                            e -> e.getKey().length() > 256 ? e.getKey().substring(0, 256) : e.getKey(),
                                            Map.Entry::getValue,
                                            (oldV, newV) -> oldV,
                                            LinkedHashMap::new
                                    )
                            );

                    save();
                }
            } catch (Exception e) {
                Constants.LOG.error("Error loading config");
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
        } catch (Exception e) {
            Constants.LOG.error("Error saving config");
        }
    }

    public static class Options {
        public String messageValue = "Hello!";
        public boolean antiFalseContact = false;
        public boolean chatQuickMessageButton = true;
        public int chatQuickMessageButtonWidth = 80;
        public boolean messageCoolingDown = true;
        public int messageCoolingDuration = 10;
        public ButtonMessageSendMode buttonMessageSendMode = ButtonMessageSendMode.CLICK_TO_SEND;

        private final ArrayList<String> message = new ArrayList<>();
        public LinkedHashMap<String, String> messageWithComment = new LinkedHashMap<>();
    }
}