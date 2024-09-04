package com.movtery.quick_chat.gui;

import com.movtery.quick_chat.config.Config;
import com.movtery.quick_chat.util.QuickChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import static com.movtery.quick_chat.QuickChat.getConfig;

public class AddMessageScreen extends Screen {
    private final Screen parent;
    private final String message;
    private EditBox messageField;

    public AddMessageScreen(Screen parent) {
        super(Component.translatable("quick_chat.gui.add_message.title"));
        this.parent = parent;
        this.message = null;
    }

    public AddMessageScreen(Screen parent, String message) {
        super(Component.translatable("quick_chat.gui.add_message.title"));
        this.parent = parent;
        this.message = message;
    }

    @Override
    protected void init() {
        this.messageField = new EditBox(this.font, this.width / 2 - 150, this.height / 2 - 50, 300, 20, Component.translatable("quick_chat.gui.add_message.title"));
        this.messageField.setMaxLength(256);
        this.messageField.setValue(this.message == null ? "" : this.message);

        this.addWidget(this.messageField);
        this.setInitialFocus(this.messageField);

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose()).bounds(this.width / 2 - 150, this.height / 2 - 14, 148, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("quick_chat.gui.add_message.add"), button -> addMessage()).bounds(this.width / 2 + 2, this.height / 2 - 14, 148, 20).build());
    }

    @Override
    public void onClose() {
        if (this.minecraft == null) return;
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.messageField.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 74, 16777215);
        guiGraphics.drawWordWrap(this.font, Component.translatable("quick_chat.config.message.desc"), this.width / 2 - 150, this.height / 2 + 24, 500, 16777215);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String message = this.messageField.getValue();
        this.init(minecraft, width, height);
        this.messageField.setValue(message);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (!QuickChatUtils.isEnter(keyCode)) {
            return false;
        } else {
            this.addMessage();
            return true;
        }
    }

    private void addMessage() {
        String message = this.messageField.getValue();
        Config config = getConfig();
        if (!message.isEmpty() && !config.getOptions().message.contains(message)) {
            if (this.message != null) {
                config.getOptions().message.remove(this.message);
            }
            config.getOptions().message.add(message);
            config.save();
        }

        this.onClose();
    }
}
