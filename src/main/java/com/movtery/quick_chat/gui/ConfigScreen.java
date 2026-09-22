package com.movtery.quick_chat.gui;

import com.mojang.serialization.Codec;
import com.movtery.quick_chat.Constants;
import com.movtery.quick_chat.core.ButtonMessageSendMode;
import com.movtery.quick_chat.util.QuickChatUtils;
import com.movtery.quick_chat.core.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
//? if <26.1 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?}
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.Button;
//? if >=1.21.11 {
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//?}
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static net.minecraft.client.Options.genericValueLabel;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final Config config = Constants.getConfig();
    private final Config.Options options = this.config.getOptions();
    private boolean textEmpty;
    private EditBox messageField;
    private Button messageListButton;
    private CycleButton<ButtonMessageSendMode> messageSendModeButton;
    private CycleButton<Boolean> antiFalseContactButton, displayAsComment, chatQuickMessageButton, messageCoolingDownButton;
    private AbstractWidget chatQuickMessageButtonHeight, cooldownDurationButton, chatQuickMessageButtonWidth;
    private CommandSuggestions commandSuggestions;

    public ConfigScreen(Screen parent) {
        super(Component.translatable("quick_chat.name"));
        this.parent = parent;
        this.textEmpty = false;
    }

    public ConfigScreen(Screen parent, boolean textEmpty) {
        this(parent);
        this.textEmpty = textEmpty;
    }

    @Override
    protected void init() {
        bindButton();

        this.addWidget(this.messageField);
        this.setInitialFocus(this.messageField);

        updateCommandInfo();

        //按钮
        this.addRenderableWidget(this.antiFalseContactButton);
        this.addRenderableWidget(this.messageListButton);
        this.addRenderableWidget(this.messageSendModeButton);
        this.addRenderableWidget(this.displayAsComment);
        this.addRenderableWidget(this.chatQuickMessageButtonHeight);
        this.addRenderableWidget(this.chatQuickMessageButton);
        this.addRenderableWidget(this.chatQuickMessageButtonWidth);
        this.addRenderableWidget(this.messageCoolingDownButton);
        this.addRenderableWidget(this.cooldownDurationButton);

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .bounds(this.width / 2 - 95, this.height - 30, 190, 20)
                .build());

        this.cooldownDurationButton.active = this.options.messageCoolingDown;
        this.chatQuickMessageButtonWidth.active = this.options.chatQuickMessageButton;
    }

    //? if <26.1 {
    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        //? if <1.20.2 {
        this.renderBackground(guiGraphics);
        //?}
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.messageField.render(guiGraphics, mouseX, mouseY, delta);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("quick_chat.config.message")
                        .append(this.textEmpty ? Component.translatable("quick_chat.config.message.empty") : Component.literal("")),
                this.width / 2 - 150 + 1, 40, this.textEmpty ? Color.RED.getRGB() : 0xFFFFFFFF); //如果消息内容为空，那么加入提醒，颜色设置为红色

        this.commandSuggestions.render(guiGraphics, mouseX, mouseY);
    }
    //?} else {
    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        this.messageField.extractWidgetRenderState(graphics, mouseX, mouseY, delta);

        graphics.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFFFFF);
        graphics.text(this.font, Component.translatable("quick_chat.config.message")
                        .append(this.textEmpty ? Component.translatable("quick_chat.config.message.empty") : Component.literal("")),
                this.width / 2 - 150 + 1, 40, this.textEmpty ? Color.RED.getRGB() : 0xFFFFFFFF); //如果消息内容为空，那么加入提醒，颜色设置为红色

        this.commandSuggestions.extractRenderState(graphics, mouseX, mouseY);
    }
    //?}

    //? if <1.21.11 {
    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        String message = this.messageField.getValue();
        this.init(minecraft, width, height);
        this.messageField.setValue(message);
        updateCommandInfo();
    }
    //?} else {
    @Override
    public void resize(int width, int height) {
        String message = this.messageField.getValue();
        this.init(width, height);
        this.messageField.setValue(message);
        updateCommandInfo();
    }
    //?}

    @Override
    public void onClose() {
        if (this.minecraft == null) return;
        if (saveText(this.minecraft)) QuickChatUtils.openScreen(this.minecraft, this.parent);
    }

    //? if <1.21.11 {
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.commandSuggestions.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else return super.keyPressed(keyCode, scanCode, modifiers);
    }
    //?} else {
    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.commandSuggestions.keyPressed(event)) {
            return true;
        } else return super.keyPressed(event);
    }
    //?}

    //? if <1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return this.commandSuggestions.mouseScrolled(delta) || super.mouseScrolled(mouseX, mouseY, delta);
    }
    //?} else {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double xAmount, double yAmount) {
        return this.commandSuggestions.mouseScrolled(yAmount) || super.mouseScrolled(mouseX, mouseY, xAmount, yAmount);
    }
    //?}

    //? if <1.21.11 {
    @Override
    public boolean mouseClicked(double d, double e, int i) {
        return this.commandSuggestions.mouseClicked(d, e, i) || super.mouseClicked(d, e, i);
    }
    //?} else {
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
        return this.commandSuggestions.mouseClicked(event) || super.mouseClicked(event, doubled);
    }
    //?}

    private void bindButton() {
        if (this.minecraft == null) return;

        //消息内容控制
        this.messageField = new EditBox(this.font, this.width / 2 - 150, 50, 300, 20, Component.translatable("quick_chat.gui.add_message.title")) {
            @Override
            protected @NotNull MutableComponent createNarrationMessage() {
                return super.createNarrationMessage().append(ConfigScreen.this.commandSuggestions.getNarrationMessage());
            }
        };
        this.commandSuggestions = new CommandSuggestions(this.minecraft, this, this.messageField, this.font, false, true, 0, 10, false, Integer.MIN_VALUE);
        this.commandSuggestions.setAllowSuggestions(true);

        this.messageField.setMaxLength(256);
        Tooltip messageTooltip = Tooltip.create(Component.translatable("quick_chat.config.message.desc"));
        this.messageField.setResponder(s -> {
            if (s.startsWith("/")) {
                this.messageField.setTooltip(null);
            } else {
                this.messageField.setTooltip(messageTooltip);
            }
            updateCommandInfo();
        });
        this.messageField.setValue(this.textEmpty ? "" : this.options.messageValue);

        int i1 = -2;
        int baseHeight = this.height / 2 + 8;
        int heightOffset = 26;

        //防误触
        this.antiFalseContactButton = getCyclingButtonWidget(this.options.antiFalseContact,
                "quick_chat.config.anti_false_contact",
                "quick_chat.config.anti_false_contact.desc",
                this.width / 2 - 150, baseHeight + (heightOffset * i1),
                (button, value) -> {
                    this.options.antiFalseContact = value;
                    this.config.save();
                });

        //快捷消息列表
        this.messageListButton = Button.builder(Component.translatable("quick_chat.gui.message_list.title"), button -> {
                    if (this.minecraft == null) return;

                    if (saveText(this.minecraft)) QuickChatUtils.openScreen(this.minecraft, new QuickMessageListScreen(this));
                }).tooltip(Tooltip.create(Component.translatable("quick_chat.config.message_list.desc")))
                .bounds(this.width / 2 + 2, baseHeight + (heightOffset * i1), 148, 20)
                .build();

        i1++;

        //快捷消息按钮发送模式
        CycleButton.Builder<ButtonMessageSendMode> sendModeBuilder;
        //? if <1.21.11 {
        sendModeBuilder = CycleButton.<ButtonMessageSendMode>builder(mode -> Component.translatable(mode.getTranslateKey()))
                .withValues(ButtonMessageSendMode.values())
                .withInitialValue(this.options.buttonMessageSendMode);
        //?} else {
        sendModeBuilder = CycleButton.builder(mode -> Component.translatable(mode.getTranslateKey()), this.options.buttonMessageSendMode)
                .withValues(ButtonMessageSendMode.values());
        //?}
        this.messageSendModeButton = sendModeBuilder
                .withTooltip((mode) -> Tooltip.create(Component.translatable(mode.getTooltipTranslateKey())))
                .create(this.width / 2 - 150, baseHeight + (heightOffset * i1), 300, 20,
                        Component.translatable("quick_chat.config.chat_button.send_mode"),
                        (button, mode) -> {
                            this.options.buttonMessageSendMode = mode;
                            this.config.save();
                        });

        i1++;

        //展示为备注
        this.displayAsComment = getCyclingButtonWidget(this.options.displayAsComment,
                "quick_chat.config.display_as_comment",
                "quick_chat.config.display_as_comment.desc",
                this.width / 2 - 150, baseHeight + (heightOffset * i1),
                (button, value) -> {
                    this.options.displayAsComment = value;
                    this.config.save();
                });

        this.chatQuickMessageButtonHeight = new OptionInstance<>("quick_chat.config.chat_button.height",
                value -> Tooltip.create(Component.translatable("quick_chat.config.chat_button.height.desc")),
                (optionText, value) -> genericValueLabel(optionText, Component.literal(value + "px")),
                new OptionInstance.IntRange(10, 30),
                Codec.INT.xmap(aInt -> 1, aInt -> 15),
                this.options.chatQuickMessageButtonHeight,
                aInt -> {
                    this.options.chatQuickMessageButtonHeight = aInt;
                    this.config.save();
                }).createButton(this.minecraft.options, this.width / 2 + 2, baseHeight + (heightOffset * i1), 148);

        i1++;

        //聊天栏内快捷消息列表
        this.chatQuickMessageButton = getCyclingButtonWidget(this.options.chatQuickMessageButton,
                "quick_chat.config.chat_button",
                "quick_chat.config.chat_button.desc",
                this.width / 2 - 150, baseHeight + (heightOffset * i1),
                (button, value) -> {
                    this.options.chatQuickMessageButton = value;
                    this.chatQuickMessageButtonWidth.active = value;
                    this.config.save();
                });

        //快捷消息按钮宽度
        this.chatQuickMessageButtonWidth = new OptionInstance<>("quick_chat.config.chat_button.width",
                value -> Tooltip.create(Component.translatable("quick_chat.config.chat_button.width.desc")),
                (optionText, value) -> genericValueLabel(optionText, Component.literal(value + "px")),
                new OptionInstance.IntRange(60, 200),
                Codec.INT.xmap(aInt -> 60, aInt -> 200),
                this.options.chatQuickMessageButtonWidth,
                aInt -> {
                    this.options.chatQuickMessageButtonWidth = aInt;
                    this.config.save();
                }).createButton(this.minecraft.options, this.width / 2 + 2, baseHeight + (heightOffset * i1), 148);

        i1++;

        //消息冷却
        this.messageCoolingDownButton = getCyclingButtonWidget(this.options.messageCoolingDown,
                "quick_chat.config.cooldown",
                "quick_chat.config.cooldown.desc",
                this.width / 2 - 150, baseHeight + (heightOffset * i1),
                (button, value) -> {
                    this.options.messageCoolingDown = value;
                    this.cooldownDurationButton.active = value;
                    this.config.save();
                });

        //消息冷却时长
        this.cooldownDurationButton = new OptionInstance<>("quick_chat.config.cooldown_duration",
                value -> Tooltip.create(Component.translatable("quick_chat.config.cooldown_duration.desc")),
                (optionText, value) -> genericValueLabel(optionText, Component.literal(value + "s")),
                new OptionInstance.IntRange(1, 15),
                Codec.INT.xmap(aInt -> 1, aInt -> 15),
                this.options.messageCoolingDuration,
                aInt -> {
                    this.options.messageCoolingDuration = aInt;
                    this.config.save();
                }).createButton(this.minecraft.options, this.width / 2 + 2, baseHeight + (heightOffset * i1), 148);
    }

    private CycleButton<Boolean> getCyclingButtonWidget(
            boolean init,
            String option,
            String tooltip,
            int x, int y,
            CycleButton.OnValueChange<Boolean> updateCallback
    ) {
        return getCyclingButtonWidget(init, option, tooltip, x, y, 148, 20, updateCallback);
    }

    private CycleButton<Boolean> getCyclingButtonWidget(
            boolean init,
            String option,
            String tooltip,
            int x, int y,
            int width, int height,
            CycleButton.OnValueChange<Boolean> updateCallback
    ) {
        return CycleButton.onOffBuilder(init)
                .withTooltip(value -> Tooltip.create(Component.translatable(tooltip)))
                .create(x, y, width, height, Component.translatable(option), updateCallback);
    }

    private void updateCommandInfo() {
        if (this.minecraft != null && this.minecraft.player != null) {
            this.commandSuggestions.updateCommandInfo();
        }
    }

    private boolean saveText(Minecraft client) {
        //切换屏幕之前需要保存文本，如果为空则不允许切换屏幕
        String text = this.messageField.getValue();
        if (text.isEmpty()) {
            QuickChatUtils.openScreen(client, new ConfigScreen(this.parent, true));
            return false;
        } else {
            this.textEmpty = false;
        }

        this.options.messageValue = text;
        this.config.save();
        return true;
    }
}
