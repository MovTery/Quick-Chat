package com.movtery.quick_chat.gui;

import com.movtery.quick_chat.Constants;
import com.movtery.quick_chat.config.Config;
import com.movtery.quick_chat.config.Message;
import com.movtery.quick_chat.util.QuickChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;

public class AddMessageScreen extends Screen {
    private final Screen parent;
    private final Message messageObject;
    private EditBox messageField;
    private EditBox commentField;
    private CommandSuggestions commandSuggestions;

    public AddMessageScreen(Screen parent) {
        super(Component.translatable("quick_chat.gui.add_message.title"));
        this.parent = parent;
        this.messageObject = null;
    }

    public AddMessageScreen(Screen parent, Message messageObject) {
        super(Component.translatable("quick_chat.gui.add_message.title"));
        this.parent = parent;
        this.messageObject = messageObject;
    }

    @Override
    protected void init() {
        this.messageField = new EditBox(this.font, this.width / 2 - 150, 50, 300, 20, Component.translatable("quick_chat.gui.add_message.title")) {
            @Override
            protected @NotNull MutableComponent createNarrationMessage() {
                return super.createNarrationMessage().append(AddMessageScreen.this.commandSuggestions.getNarrationMessage());
            }
        };
        this.commandSuggestions = new CommandSuggestions(this.minecraft, this, this.messageField, this.font, false, true, 0, 10, false, Integer.MIN_VALUE);
        this.commandSuggestions.setAllowSuggestions(true);

        this.messageField.setMaxLength(256);
        this.messageField.setResponder(s -> updateCommandInfo());
        this.messageField.setValue(this.messageObject == null ? "" : this.messageObject.getMessage());

        this.commentField = new EditBox(this.font, this.width / 2 - 150, this.height - 100, 300, 20, Component.translatable("quick_chat.config.comment")) {
            @Override
            protected @NotNull MutableComponent createNarrationMessage() {
                return super.createNarrationMessage();
            }
        };

        this.commentField.setMaxLength(Integer.MAX_VALUE);
        this.commentField.setValue(this.messageObject == null ? "" : this.messageObject.getComment());

        this.addWidget(this.messageField);
        this.addWidget(this.commentField);

        this.setInitialFocus(this.messageField);

        updateCommandInfo();

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose()).bounds(this.width / 2 - 150, this.height - 30, 148, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("quick_chat.gui.add_message.add"), button -> addMessage()).bounds(this.width / 2 + 2, this.height - 30, 148, 20).build());
    }

    @Override
    public void onClose() {
        if (this.minecraft == null) return;
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics);

        super.render(guiGraphics, mouseX, mouseY, delta);
        this.messageField.render(guiGraphics, mouseX, mouseY, delta);

        guiGraphics.drawWordWrap(this.font, Component.translatable("quick_chat.config.comment"), this.width / 2 - 150, this.height - 110, 500, 16777215);
        this.commentField.render(guiGraphics, mouseX, mouseY, delta);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
        guiGraphics.drawWordWrap(this.font, Component.translatable("quick_chat.config.message.desc"), this.width / 2 - 150, this.height - 70, 500, 16777215);

        this.commandSuggestions.render(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        String message = this.messageField.getValue();
        String comment = this.commentField.getValue();
        this.init(minecraft, width, height);
        this.messageField.setValue(message);
        this.commentField.setValue(comment);
        updateCommandInfo();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.commandSuggestions.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (!QuickChatUtils.isEnter(keyCode)) {
            return false;
        } else {
            this.addMessage();
            return true;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return this.commandSuggestions.mouseScrolled(delta) || super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        return this.commandSuggestions.mouseClicked(d, e, i) || super.mouseClicked(d, e, i);
    }

    private void updateCommandInfo() {
        if (this.minecraft != null && this.minecraft.player != null) {
            this.commandSuggestions.updateCommandInfo();
        }
    }

    private void addMessage() {
        String message = this.messageField.getValue();
        String comment = this.commentField.getValue();
        Config config = Constants.getConfig();
        ArrayList<Message> messageWithComment = config.getOptions().messageWithComment;
        if (!message.isEmpty() && !comment.isEmpty()) {
            Optional<Message> optional = messageWithComment.stream()
                    .filter(m -> m.equals(this.messageObject))
                    .findFirst();

            if (optional.isPresent()) {
                Message originObject = optional.get();
                originObject.setMessage(message);
                originObject.setComment(comment);
            } else {
                messageWithComment.add(new Message(message, comment));
            }
            config.save();
        }

        this.onClose();
    }
}
