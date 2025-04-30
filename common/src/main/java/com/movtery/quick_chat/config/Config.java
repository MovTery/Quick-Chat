package com.movtery.quick_chat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.movtery.quick_chat.Constants;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Config {
    public static final int[] messageCoolingDurationRange = {1, 15};
    public static final int[] chatQuickMessageButtonWidthRange = {60, 200};
    private final File file;
    private final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

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
                            options.messageWithComment.add(new Message(item, ""));
                        }
                        options.message.clear();
                    }

                    List<Message> filteredAndDistinct = options.messageWithComment.stream()
                            .filter(message -> message.getMessage().length() <= 256)
                            .distinct()
                            .toList();

                    options.messageWithComment.clear();
                    options.messageWithComment.addAll(filteredAndDistinct);
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
        @Expose public String messageValue = "Hello!";
        @Expose public boolean antiFalseContact = false;
        @Expose public boolean chatQuickMessageButton = true;
        @Expose public int chatQuickMessageButtonWidth = 80;
        @Expose public boolean messageCoolingDown = true;
        @Expose public int messageCoolingDuration = 10;
        @Expose public ButtonMessageSendMode buttonMessageSendMode = ButtonMessageSendMode.CLICK_TO_SEND;

        @Expose(serialize = false)
        private final ArrayList<String> message = new ArrayList<>();

        @Expose public ArrayList<Message> messageWithComment = new ArrayList<>();
    }
}