package com.movtery.quick_chat.gui;

import com.mojang.serialization.Codec;
import com.movtery.quick_chat.Constants;
import com.movtery.quick_chat.config.ButtonMessageSendMode;
import com.movtery.quick_chat.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.Button;
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
    private CycleButton<Boolean> antiFalseContactButton, chatQuickMessageButton, messageCoolingDownButton;
    private AbstractWidget cooldownDurationButton, chatQuickMessageButtonWidth;
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

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.messageField.render(guiGraphics, mouseX, mouseY, delta);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
        guiGraphics.drawString(this.font, Component.translatable("quick_chat.config.message")
                        .append(this.textEmpty ? Component.translatable("quick_chat.config.message.empty") : Component.literal("")),
                this.width / 2 - 150 + 1, 40, this.textEmpty ? Color.RED.getRGB() : 16777215); //如果消息内容为空，那么加入提醒，颜色设置为红色

        this.commandSuggestions.render(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        String message = this.messageField.getValue();
        this.init(minecraft, width, height);
        this.messageField.setValue(message);
        updateCommandInfo();
    }

    @Override
    public void onClose() {
        if (this.minecraft == null) return;
        if (saveText(this.minecraft)) this.minecraft.setScreen(this.parent);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.commandSuggestions.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f, double g) {
        return this.commandSuggestions.mouseScrolled(g) || super.mouseScrolled(d, e, f, g);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        return this.commandSuggestions.mouseClicked(d, e, i) || super.mouseClicked(d, e, i);
    }

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

        //防误触
        this.antiFalseContactButton = getCyclingButtonWidget(this.options.antiFalseContact,
                "quick_chat.config.anti_false_contact",
                "quick_chat.config.anti_false_contact.desc",
                this.width / 2 - 150, this.height / 2 - 26,
                (button, value) -> {
                    this.options.antiFalseContact = value;
                    this.config.save();
                });

        //快捷消息列表
        this.messageListButton = Button.builder(Component.translatable("quick_chat.gui.message_list.title"), button -> {
                    if (this.minecraft == null) return;

                    if (saveText(this.minecraft)) this.minecraft.setScreen(new QuickMessageListScreen(this));
                }).tooltip(Tooltip.create(Component.translatable("quick_chat.config.message_list.desc")))
                .bounds(this.width / 2 + 2, this.height / 2 - 26, 148, 20)
                .build();

        //快捷消息按钮发送模式
        CycleButton.Builder<ButtonMessageSendMode> sendModeBuilder = CycleButton.builder(mode -> Component.translatable(mode.getTranslateKey()));
        this.messageSendModeButton = sendModeBuilder
                .withValues(ButtonMessageSendMode.values())
                .withInitialValue(this.options.buttonMessageSendMode)
                .withTooltip((mode) -> Tooltip.create(Component.translatable(mode.getTooltipTranslateKey())))
                .create(this.width / 2 - 150, this.height / 2, 300, 20,
                        Component.translatable("quick_chat.config.chat_button.send_mode"),
                        (button, mode) -> {
                            this.options.buttonMessageSendMode = mode;
                            this.config.save();
                        });

        //聊天栏内快捷消息列表
        this.chatQuickMessageButton = getCyclingButtonWidget(this.options.chatQuickMessageButton,
                "quick_chat.config.chat_button",
                "quick_chat.config.chat_button.desc",
                this.width / 2 - 150, this.height / 2 + 26,
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
                }).createButton(this.minecraft.options, this.width / 2 + 2, this.height / 2 + 26, 148);

        //消息冷却
        this.messageCoolingDownButton = getCyclingButtonWidget(this.options.messageCoolingDown,
                "quick_chat.config.cooldown",
                "quick_chat.config.cooldown.desc",
                this.width / 2 - 150, this.height / 2 + 52,
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
                }).createButton(this.minecraft.options, this.width / 2 + 2, this.height / 2 + 52, 148);
    }

    private CycleButton<Boolean> getCyclingButtonWidget(boolean init, String option, String tooltip, int x, int y, CycleButton.OnValueChange<Boolean> updateCallback) {
        return CycleButton.onOffBuilder(init)
                .withTooltip(value -> Tooltip.create(Component.translatable(tooltip)))
                .create(x, y, 148, 20, Component.translatable(option), updateCallback);
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
            client.setScreen(new ConfigScreen(this.parent, true));
            return false;
        } else {
            this.textEmpty = false;
        }

        this.options.messageValue = text;
        this.config.save();
        return true;
    }
}