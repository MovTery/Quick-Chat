package com.movtery.quick_chat.gui;

import com.movtery.quick_chat.config.Config;
import com.movtery.quick_chat.util.LastMessage;
import com.movtery.quick_chat.util.QuickChatUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import static com.movtery.quick_chat.QuickChat.getConfig;

public class QuickMessageListScreen extends Screen {
    private final Screen parent;
    private final Config config = getConfig();
    private Button doneButton, removeButton, editButton, addButton, sendButton;
    private MessageListWidget messageListWidget;

    public QuickMessageListScreen(Screen parent) {
        super(Component.translatable("quick_chat.gui.message_list.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        bindValues();
        this.messageListWidget = this.addRenderableWidget(new MessageListWidget(this.minecraft));
        this.addRenderableWidget(this.doneButton);
        this.addRenderableWidget(this.removeButton);
        this.addRenderableWidget(this.editButton);
        this.addRenderableWidget(this.addButton);
        this.addRenderableWidget(this.sendButton);
    }

    private void bindValues() {
        doneButton = Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose()).bounds(this.width / 2 - 208, this.height - 38, 80, 20).build();
        removeButton = Button.builder(Component.translatable("quick_chat.gui.message_list.remove"), (button) -> this.remove()).bounds(this.width / 2 - 124, this.height - 38, 80, 20).build();
        editButton = Button.builder(Component.translatable("quick_chat.gui.message_list.edit"), (button) -> this.edit()).bounds(this.width / 2 - 40, this.height - 38, 80, 20).build();
        addButton = Button.builder(Component.translatable("quick_chat.gui.message_list.add"), (button) -> this.addMessage()).bounds(this.width / 2 + 44, this.height - 38, 80, 20).build();
        sendButton = Button.builder(Component.translatable("quick_chat.gui.message_list.send"), (button) -> this.onDone()).bounds(this.width / 2 + 128, this.height - 38, 80, 20).build();

        if (Minecraft.getInstance().player == null) {
            sendButton.setTooltip(Tooltip.create(Component.translatable("quick_chat.gui.message_list.send_not_in_game")));
            sendButton.active = false;
        }
    }

    @Override
    public void onClose() {
        if (this.minecraft == null) return;
        this.minecraft.setScreen(parent);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        TreeSet<String> message = config.getOptions().message;
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 16, 16777215);
        guiGraphics.drawCenteredString(this.font,
                Component.literal(String.format("(%d) ", this.messageListWidget.children().size())).withStyle(ChatFormatting.YELLOW)
                        .append(message.isEmpty() ?
                                Component.translatable("quick_chat.gui.message_list.tip_empty").withStyle(ChatFormatting.RED) :
                                Component.translatable("quick_chat.gui.message_list.tip").withStyle(ChatFormatting.WHITE)),
                this.width / 2, this.height - 54, 16777215);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (Minecraft.getInstance().player != null && QuickChatUtils.isEnter(keyCode)) {
            this.onDone();
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void onDone() {
        MessageListWidget.MessageListEntry messageListEntry = this.messageListWidget.getSelected();
        if (this.minecraft == null) onClose();

        if (messageListEntry != null) {
            QuickChatUtils.sendMessage(this.minecraft, messageListEntry.message.get(messageListEntry.abbreviatedText));
        }

        this.onClose();
    }

    private void remove() {
        MessageListWidget.MessageListEntry messageListEntry = this.messageListWidget.getSelected();
        if (messageListEntry != null) {
            TreeSet<String> message = config.getOptions().message;
            if (!message.isEmpty()) {
                message.remove(messageListEntry.message.get(messageListEntry.abbreviatedText));
            }
            config.save();
        }

        if (this.minecraft != null) {
            this.minecraft.setScreen(this);
        }
    }

    private void addMessage() {
        if (this.minecraft == null) return;
        this.minecraft.setScreen(new AddMessageScreen(this));
    }

    private void edit() {
        if (this.minecraft == null) return;
        MessageListWidget.MessageListEntry messageListEntry = this.messageListWidget.getSelected();
        if (messageListEntry != null) {
            this.minecraft.setScreen(new AddMessageScreen(this, messageListEntry.message.get(messageListEntry.abbreviatedText)));
        }
    }

    private class MessageListWidget extends ObjectSelectionList<MessageListWidget.MessageListEntry> {
        public MessageListWidget(Minecraft client) {
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

        public class MessageListEntry extends ObjectSelectionList.Entry<MessageListEntry> {
            final Map<String, String> message = new HashMap<>();
            final String abbreviatedText;
            private final WidgetTooltipHolder tooltip = new WidgetTooltipHolder();
            private long clickTime;

            public MessageListEntry(String message) {
                this.abbreviatedText = QuickChatUtils.getAbbreviatedText(message, minecraft, QuickMessageListScreen.this.width / 2 - 8);
                this.message.put(this.abbreviatedText, message);
                this.tooltip.set(Tooltip.create(Component.literal(message)));
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                guiGraphics.drawCenteredString(minecraft.font, this.abbreviatedText, MessageListWidget.this.width / 2, y + 1, 16777215);
                this.tooltip.refreshTooltipForNextRenderPass(this.isMouseOver(mouseX, mouseY), this.isFocused(), this.getRectangle());
            }

            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                this.onPressed();
                long millis = Util.getMillis();
                if (Minecraft.getInstance().player != null && millis - this.clickTime < 250L) {
                    QuickMessageListScreen.this.onDone();
                }

                this.clickTime = millis;
                return true;
            }

            void onPressed() {
                MessageListWidget.this.setSelected(this);
            }

            public @NotNull Component getNarration() {
                return Component.literal(this.message.get(abbreviatedText));
            }
        }
    }
}
