package com.movtery.quick_chat.core;

import com.movtery.quick_chat.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

public class Config {
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
                    for (WheelDirection direction : WheelDirection.values()) {
                        Message message = options.wheelMessages.get(direction);
                        if (message == null || message.getMessage() == null) {
                            options.wheelMessages.put(direction, new Message("", ""));
                            continue;
                        }
                        if (message.getMessage().length() > 256) {
                            message.setMessage(message.getMessage().substring(0, 256));
                        }
                        if (message.getComment() == null) {
                            message.setComment("");
                        }
                    }

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

                    int buttonWidth = options.getChatButtonWidth();
                    if (options.chatQuickMessageButtonWidth != buttonWidth) {
                        options.chatQuickMessageButtonWidth = buttonWidth;
                    }

                    int buttonHeight = options.getChatButtonHeight();
                    if (options.chatQuickMessageButtonHeight != buttonHeight) {
                        options.chatQuickMessageButtonHeight = buttonHeight;
                    }

                    int coolingDuration = options.getCoolingDuration();
                    if (options.messageCoolingDuration != coolingDuration) {
                        options.messageCoolingDuration = coolingDuration;
                    }

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
        @Expose public EnumMap<WheelDirection, Message> wheelMessages = createEmptyWheelMessages();
        @Expose public boolean antiFalseContact = false;
        @Expose public boolean chatQuickMessageButton = true;
        @Expose public int chatQuickMessageButtonWidth = 80;
        @Expose public boolean messageCoolingDown = true;
        @Expose public int messageCoolingDuration = 10;
        @Expose public boolean displayAsComment = false;
        @Expose public int chatQuickMessageButtonHeight = 20;
        @Expose public ButtonMessageSendMode buttonMessageSendMode = ButtonMessageSendMode.CLICK_TO_SEND;

        @Expose(serialize = false)
        private final ArrayList<String> message = new ArrayList<>();

        @Expose public ArrayList<Message> messageWithComment = new ArrayList<>();

        public static EnumMap<WheelDirection, Message> createEmptyWheelMessages() {
        EnumMap<WheelDirection, Message> map = new EnumMap<>(WheelDirection.class);
        for (WheelDirection direction : WheelDirection.values()) {
            map.put(direction, new Message("", ""));
        }
        return map;
    }

    public int getChatButtonWidth() {
            int[] widthRange = {60, 200};
            return this.chatQuickMessageButtonWidth > widthRange[1] ? widthRange[1] : Math.max(this.chatQuickMessageButtonWidth, widthRange[0]);
        }

        public int getChatButtonHeight() {
            int[] heightRange = {10, 30};
            return this.chatQuickMessageButtonHeight > heightRange[1] ? heightRange[1] : Math.max(this.chatQuickMessageButtonHeight, heightRange[0]);
        }

        public int getCoolingDuration() {
            int[] durationRange = {1, 15};
            return this.messageCoolingDuration > durationRange[1] ? durationRange[1] : Math.max(this.messageCoolingDuration, durationRange[0]);
        }
    }
}
