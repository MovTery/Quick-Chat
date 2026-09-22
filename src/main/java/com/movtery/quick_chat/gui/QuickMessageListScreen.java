package com.movtery.quick_chat.gui;

import com.movtery.quick_chat.Constants;
import com.movtery.quick_chat.core.Config;
import com.movtery.quick_chat.core.Message;
import com.movtery.quick_chat.util.LastMessage;
import com.movtery.quick_chat.util.QuickChatUtils;
import net.minecraft.ChatFormatting;
//? if <1.21.11 {
/*import net.minecraft.Util;
*///?} else {
import net.minecraft.util.Util;
//?}
import net.minecraft.client.Minecraft;
//? if <26.1 {
/*import net.minecraft.client.gui.GuiGraphics;
*///?} else {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?}
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
//? if >=1.21.9 {
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//?}
import net.minecraft.client.gui.screens.Screen;
//? if <1.21.6 {
/*import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
*///?}
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class QuickMessageListScreen extends Screen {
    private final Screen parent;
    private final Config config = Constants.getConfig();
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
        QuickChatUtils.openScreen(this.minecraft, parent);
    }

    //? if <26.1 {
    /*@Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        //? if <1.20.2 {
        /^this.renderBackground(guiGraphics);
        ^///?}
        super.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 16, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font,
                Component.literal(String.format("(%d) ", this.messageListWidget.children().size())).withStyle(ChatFormatting.YELLOW)
                        .append(config.getOptions().messageWithComment.isEmpty() ?
                                Component.translatable("quick_chat.gui.message_list.tip_empty").withStyle(ChatFormatting.RED) :
                                Component.translatable("quick_chat.gui.message_list.tip").withStyle(ChatFormatting.WHITE)),
                this.width / 2, this.height - 54, 0xFFFFFFFF);
    }
    *///?} else {
    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.centeredText(this.font, this.title, this.width / 2, 16, 0xFFFFFFFF);
        graphics.centeredText(this.font,
                Component.literal(String.format("(%d) ", this.messageListWidget.children().size())).withStyle(ChatFormatting.YELLOW)
                        .append(config.getOptions().messageWithComment.isEmpty() ?
                                Component.translatable("quick_chat.gui.message_list.tip_empty").withStyle(ChatFormatting.RED) :
                                Component.translatable("quick_chat.gui.message_list.tip").withStyle(ChatFormatting.WHITE)),
                this.width / 2, this.height - 54, 0xFFFFFFFF);
    }
    //?}

    //? if <1.21.9 {
    /*@Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (Minecraft.getInstance().player != null && QuickChatUtils.isEnter(keyCode)) {
            this.onDone();
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    *///?} else {
    @Override
    public boolean keyPressed(KeyEvent event) {
        if (Minecraft.getInstance().player != null && QuickChatUtils.isEnter(event.key())) {
            this.onDone();
        }

        return super.keyPressed(event);
    }
    //?}

    private void onDone() {
        MessageListWidget.MessageListEntry messageListEntry = this.messageListWidget.getSelected();
        if (this.minecraft == null) onClose();

        if (messageListEntry != null) {
            QuickChatUtils.sendMessage(this.minecraft, messageListEntry.getMessage().getMessage());
        }

        this.onClose();
    }

    private void remove() {
        MessageListWidget.MessageListEntry messageListEntry = this.messageListWidget.getSelected();
        if (messageListEntry != null) {
            ArrayList<Message> messageWithComment = config.getOptions().messageWithComment;
            if (!messageWithComment.isEmpty()) {
                messageWithComment.remove(messageListEntry.getMessage());
            }
            config.save();
        }

        if (this.minecraft != null) {
            QuickChatUtils.openScreen(this.minecraft, this);
        }
    }

    private void addMessage() {
        if (this.minecraft == null) return;
        QuickChatUtils.openScreen(this.minecraft, new AddMessageScreen(this));
    }

    private void edit() {
        if (this.minecraft == null) return;
        MessageListWidget.MessageListEntry messageListEntry = this.messageListWidget.getSelected();
        if (messageListEntry != null) {
            QuickChatUtils.openScreen(this.minecraft, new AddMessageScreen(this, messageListEntry.getMessage()));
        }
    }

    private class MessageListWidget extends ObjectSelectionList<MessageListWidget.MessageListEntry> {
        public MessageListWidget(Minecraft client) {
            //? if <1.20.2 {
            /*super(client, QuickMessageListScreen.this.width, QuickMessageListScreen.this.height - 93, 32, QuickMessageListScreen.this.height - 65 + 4, 18);
            *///?} else {
            super(client, QuickMessageListScreen.this.width, QuickMessageListScreen.this.height - 93, 32, 18);
            //?}
            reloadMessages(true);
        }

        private void reloadMessages(boolean first) {
            if (!first) this.clearEntries();
            ArrayList<Message> messageList = config.getOptions().messageWithComment;

            boolean messageIsEmpty = messageList.isEmpty();

            if (!messageIsEmpty) {
                AtomicInteger i = new AtomicInteger();
                messageList.forEach(message -> {

                    MessageListEntry entry = new MessageListEntry(this, message, i.get());
                    this.addEntry(entry);
                    if (i.get() == 0 || Objects.equals(message.getMessage(), LastMessage.getInstance().getLastMessage())) {
                        this.setSelected(entry);
                    }
                    i.getAndIncrement();
                });
            }

            QuickMessageListScreen.this.removeButton.active = !messageIsEmpty;
            QuickMessageListScreen.this.editButton.active = !messageIsEmpty;
            QuickMessageListScreen.this.sendButton.active = Minecraft.getInstance().player != null && !messageIsEmpty;
        }

        public void moveEntryUp(MessageListEntry entry) {
            int index = entry.index;
            if (index > 0) {
                moveEntry(index, index - 1);
            }
        }

        public void moveEntryDown(MessageListEntry entry) {
            int index = entry.index;
            if (index < config.getOptions().messageWithComment.size() - 1) {
                moveEntry(index, index + 1);
            }
        }

        private void moveEntry(int index1, int index2) {
            ArrayList<Message> messageWithComment = config.getOptions().messageWithComment;
            Collections.swap(messageWithComment, index1, index2);
            config.save();
            reloadMessages(false);
        }

        public class MessageListEntry extends Entry<MessageListEntry> {
            private final MessageListWidget list;
            private final Message message;
            final String abbreviatedText;
            private final Component tooltip;
            private long clickTime;
            final int index;

            public MessageListEntry(MessageListWidget listWidget, Message messageObject, int index) {
                this.list = listWidget;
                this.message = messageObject;
                this.index = index;
                String showOnEntry;
                if (config.getOptions().displayAsComment && !messageObject.getComment().isEmpty()) {
                    showOnEntry = messageObject.getComment();
                } else {
                    showOnEntry = messageObject.getMessage();
                }
                this.abbreviatedText = QuickChatUtils.getAbbreviatedText(showOnEntry, minecraft, list.getRowWidth() - 30);
                this.tooltip = QuickChatUtils.getMessageComponent(messageObject);
            }

            public Message getMessage() {
                return this.message;
            }

            //? if <1.21.6 {
            /*@Override
            public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                renderEntry(guiGraphics, mouseX, mouseY, y + 2);

                if (this.isMouseOver(mouseX, mouseY)) {
                    Screen screen = Minecraft.getInstance().screen;
                    if (screen != null) screen.setTooltipForNextRenderPass(Tooltip.create(this.tooltip), DefaultTooltipPositioner.INSTANCE, this.isFocused());
                }
            }
            *///?}
            //? if >=1.21.6 && <1.21.9 {
            /*@Override
            public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                renderEntry(guiGraphics, mouseX, mouseY, y + 2);

                if (this.isMouseOver(mouseX, mouseY)) {
                    guiGraphics.setTooltipForNextFrame(QuickChatUtils.splitTooltipLines(this.tooltip), mouseX, mouseY);
                }
            }
            *///?}
            //? if >=1.21.9 && <26.1 {
            /*@Override
            public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                renderEntry(guiGraphics, mouseX, mouseY, this.getContentY());

                if (this.isMouseOver(mouseX, mouseY)) {
                    guiGraphics.setTooltipForNextFrame(QuickChatUtils.splitTooltipLines(this.tooltip), mouseX, mouseY);
                }
            }
            *///?}
            //? if >=26.1 {
            @Override
            public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                renderEntry(graphics, mouseX, mouseY, this.getContentY());

                if (this.isMouseOver(mouseX, mouseY)) {
                    graphics.setTooltipForNextFrame(QuickChatUtils.splitTooltipLines(this.tooltip), mouseX, mouseY);
                }
            }
            //?}

            //? if <26.1 {
            /*private void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, int textY) {
                guiGraphics.drawCenteredString(minecraft.font, this.abbreviatedText, MessageListWidget.this.width / 2, textY, 0xFFFFFFFF);

                int entryX = list.getRowLeft() + list.getRowWidth();

                guiGraphics.drawString(minecraft.font, "↓", entryX - 11, textY, 0xFFFFFFFF);
                guiGraphics.drawString(minecraft.font, "↑", entryX - 20, textY, 0xFFFFFFFF);
            }
            *///?} else {
            private void renderEntry(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int textY) {
                graphics.centeredText(minecraft.font, this.abbreviatedText, MessageListWidget.this.width / 2, textY, 0xFFFFFFFF);

                int entryX = list.getRowLeft() + list.getRowWidth();

                graphics.text(minecraft.font, "↓", entryX - 11, textY, 0xFFFFFFFF);
                graphics.text(minecraft.font, "↑", entryX - 20, textY, 0xFFFFFFFF);
            }
            //?}

            //? if <1.21.9 {
            /*public boolean mouseClicked(double mouseX, double mouseY, int button) {
                this.onPressed();
                long millis = Util.getMillis();

                int entryX = list.getRowLeft() + list.getRowWidth();

                if (mouseX >= entryX - 20 && mouseX <= entryX - 12) {
                    moveEntryUp(this);
                    return true;
                } else if (mouseX >= entryX - 12 && mouseX <= entryX) {
                    moveEntryDown(this);
                    return true;
                } else {
                    if (Minecraft.getInstance().player != null && millis - this.clickTime < 250L) {
                        QuickMessageListScreen.this.onDone();
                    }
                    this.clickTime = millis;
                }

                return true;
            }
            *///?} else {
            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
                this.onPressed();
                long millis = Util.getMillis();
                double mouseX = event.x();

                int entryX = list.getRowLeft() + list.getRowWidth();

                if (mouseX >= entryX - 20 && mouseX <= entryX - 12) {
                    moveEntryUp(this);
                    return true;
                } else if (mouseX >= entryX - 12 && mouseX <= entryX) {
                    moveEntryDown(this);
                    return true;
                } else {
                    if (Minecraft.getInstance().player != null && millis - this.clickTime < 250L) {
                        QuickMessageListScreen.this.onDone();
                    }
                    this.clickTime = millis;
                }

                return true;
            }
            //?}

            void onPressed() {
                MessageListWidget.this.setSelected(this);
            }

            public @NotNull Component getNarration() {
                return QuickChatUtils.getMessageComponent(this.message);
            }
        }
    }
}
