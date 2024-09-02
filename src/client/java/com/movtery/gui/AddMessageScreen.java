package com.movtery.gui;

import com.movtery.config.Config;
import com.movtery.util.QuickChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import static com.movtery.QuickChatClient.getConfig;
import static net.minecraft.screen.ScreenTexts.CANCEL;

public class AddMessageScreen extends Screen {
    private final Screen parent;
    private final String message;
    private TextFieldWidget messageField;

    public AddMessageScreen(Screen parent) {
        super(Text.translatable("quick_chat.gui.add_message.title"));
        this.parent = parent;
        this.message = null;
    }

    public AddMessageScreen(Screen parent, String message) {
        super(Text.translatable("quick_chat.gui.add_message.title"));
        this.parent = parent;
        this.message = message;
    }

    @Override
    protected void init() {
        this.messageField = new TextFieldWidget(this.textRenderer, this.width / 2 - 150, this.height / 2 - 50, 300, 20, Text.translatable("quick_chat.gui.add_message.title"));
        this.messageField.setMaxLength(256);
        this.messageField.setText(this.message == null ? "" : this.message);

        this.addSelectableChild(this.messageField);
        this.setInitialFocus(this.messageField);

        this.addDrawableChild(ButtonWidget.builder(CANCEL, button -> close()).dimensions(this.width / 2 - 150, this.height / 2 - 14, 148, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("quick_chat.gui.add_message.add"), button -> addMessage()).dimensions(this.width / 2 + 2, this.height / 2 - 14, 148, 20).build());
    }

    @Override
    public void close() {
        if (this.client == null) return;
        this.client.setScreen(this.parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.messageField.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 74, 16777215);
        context.drawTextWrapped(this.textRenderer, Text.translatable("quick_chat.config.message.desc"), this.width / 2 - 150, this.height / 2 + 24, 500, 16777215);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.messageField.getText();
        this.init(client, width, height);
        this.messageField.setText(string);
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
        String message = this.messageField.getText();
        Config config = getConfig();
        if (!message.isEmpty() && !config.getOptions().message.contains(message)) {
            if (this.message != null) {
                config.getOptions().message.remove(this.message);
            }
            config.getOptions().message.add(message);
            config.save();
        }

        this.close();
    }
}
