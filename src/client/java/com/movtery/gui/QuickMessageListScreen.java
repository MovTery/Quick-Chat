package com.movtery.gui;

import com.movtery.config.Config;
import com.movtery.util.LastMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import static com.movtery.util.QuickChatUtils.*;

@Environment(EnvType.CLIENT)
public class QuickMessageListScreen extends Screen {
    private final Screen parent;
    private final Config config = getConfig();
    private ButtonWidget doneButton, removeButton, editButton, addButton, sendButton;
    private MessageListWidget messageListWidget;

    public QuickMessageListScreen(Screen parent) {
        super(Text.translatable("quick_chat.gui.message_list.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        bindValues();
        this.messageListWidget = this.addDrawableChild(new MessageListWidget(this.client));
        this.addDrawableChild(this.doneButton);
        this.addDrawableChild(this.removeButton);
        this.addDrawableChild(this.editButton);
        this.addDrawableChild(this.addButton);
        this.addDrawableChild(this.sendButton);
    }

    private void bindValues() {
        doneButton = ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.close()).dimensions(this.width / 2 - 208, this.height - 38, 80, 20).build();
        removeButton = ButtonWidget.builder(Text.translatable("quick_chat.gui.message_list.remove"), (button) -> this.remove()).dimensions(this.width / 2 - 124, this.height - 38, 80, 20).build();
        editButton = ButtonWidget.builder(Text.translatable("quick_chat.gui.message_list.edit"), (button) -> this.edit()).dimensions(this.width / 2 - 40, this.height - 38, 80, 20).build();
        addButton = ButtonWidget.builder(Text.translatable("quick_chat.gui.message_list.add"), (button) -> this.addMessage()).dimensions(this.width / 2 + 44, this.height - 38, 80, 20).build();
        sendButton = ButtonWidget.builder(Text.translatable("quick_chat.gui.message_list.send"), (button) -> this.onDone()).dimensions(this.width / 2 + 128, this.height - 38, 80, 20).build();

        if (MinecraftClient.getInstance().player == null) {
            sendButton.setTooltip(Tooltip.of(Text.translatable("quick_chat.gui.message_list.send_not_in_game")));
            sendButton.active = false;
        }
    }

    @Override
    public void close() {
        if (this.client == null) return;
        this.client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        TreeSet<String> message = config.getOptions().message;
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 16, 16777215);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal(String.format("(%d) ", this.messageListWidget.children().size())).formatted(Formatting.YELLOW)
                .append(message.isEmpty() ?
                        Text.translatable("quick_chat.gui.message_list.tip_empty").formatted(Formatting.RED) :
                        Text.translatable("quick_chat.gui.message_list.tip").formatted(Formatting.WHITE)),
                this.width / 2, this.height - 54, 16777215);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (MinecraftClient.getInstance().player != null && KeyCodes.isToggle(keyCode)) {
            this.onDone();
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void onDone() {
        MessageListWidget.MessageListEntry messageListEntry = this.messageListWidget.getSelectedOrNull();
        if (this.client == null) close();

        if (messageListEntry != null && this.client.player != null) {
            sendMessage(this.client.player, messageListEntry.message.get(messageListEntry.key));
        }

        this.close();
    }

    private void remove() {
        MessageListWidget.MessageListEntry messageListEntry = this.messageListWidget.getSelectedOrNull();
        if (messageListEntry != null) {
            TreeSet<String> message = config.getOptions().message;
            if (!message.isEmpty()) {
                message.remove(messageListEntry.message.get(messageListEntry.key));
            }
            config.save();
        }

        if (this.client != null) {
            this.client.setScreen(this);
        }
    }

    private void addMessage() {
        if (this.client == null) return;
        this.client.setScreen(new AddMessageScreen(this));
    }

    private void edit() {
        if (this.client == null) return;
        MessageListWidget.MessageListEntry messageListEntry = this.messageListWidget.getSelectedOrNull();
        if (messageListEntry != null) {
            this.client.setScreen(new AddMessageScreen(this, messageListEntry.message.get(messageListEntry.key)));
        }
    }

    @Environment(EnvType.CLIENT)
    private class MessageListWidget extends AlwaysSelectedEntryListWidget<MessageListWidget.MessageListEntry> {
        public MessageListWidget(MinecraftClient client) {
            super(client, QuickMessageListScreen.this.width, QuickMessageListScreen.this.height - 93, 32, 18);
            TreeSet<String> message = config.getOptions().message;
            if (!message.isEmpty()) {
                AtomicInteger i = new AtomicInteger();
                message.forEach(s -> {
                    MessageListEntry entry = new MessageListEntry(s);
                    this.addEntry(entry);
                    if (i.get() == 0 || Objects.equals(s, LastMessage.getInstance().getLastMessage()))
                        this.setSelected(entry);
                    i.getAndIncrement();
                });
            } else {
                QuickMessageListScreen.this.removeButton.active = false;
                QuickMessageListScreen.this.editButton.active = false;
                QuickMessageListScreen.this.sendButton.active = false;
            }
        }

        @Override
        public int getRowWidth() {
            return this.width / 2 + 8;
        }

        @Environment(EnvType.CLIENT)
        public class MessageListEntry extends AlwaysSelectedEntryListWidget.Entry<MessageListWidget.MessageListEntry> {
            final Map<String, String> message = new HashMap<>();
            final String key;
            private long clickTime;

            public MessageListEntry(String message) {
                this.key = getAbbreviatedText(message, client, QuickMessageListScreen.this.width / 2 - 8);
                this.message.put(this.key, message);
            }

            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                context.drawCenteredTextWithShadow(client.textRenderer, this.key, MessageListWidget.this.width / 2, y + 1, 16777215);
            }

            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                this.onPressed();
                if (MinecraftClient.getInstance().player != null && Util.getMeasuringTimeMs() - this.clickTime < 250L) {
                    QuickMessageListScreen.this.onDone();
                }

                this.clickTime = Util.getMeasuringTimeMs();
                return true;
            }

            void onPressed() {
                MessageListWidget.this.setSelected(this);
            }

            public Text getNarration() {
                return Text.literal(this.message.get(key));
            }
        }
    }
}
