package com.movtery.quick_chat.gui;

import com.mojang.serialization.Codec;
import com.movtery.quick_chat.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.awt.*;

import static com.movtery.quick_chat.QuickChat.getConfig;
import static net.minecraft.client.Options.genericValueLabel;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final Config config = getConfig();
    private final Config.Options options = this.config.getOptions();
    private boolean textEmpty;
    private EditBox textField;
    private Button messageListButton;
    private CycleButton<Boolean> antiFalseContactButton, chatQuickMessageButton, messageCoolingDownButton;
    private AbstractWidget cooldownDurationButton, chatQuickMessageButtonWidth;

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

        this.addWidget(this.textField);
        this.setInitialFocus(this.textField);

        //按钮
        this.addRenderableWidget(this.antiFalseContactButton);
        this.addRenderableWidget(this.messageListButton);
        this.addRenderableWidget(this.chatQuickMessageButton);
        this.addRenderableWidget(this.chatQuickMessageButtonWidth);
        this.addRenderableWidget(this.messageCoolingDownButton);
        this.addRenderableWidget(this.cooldownDurationButton);

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .bounds(this.width / 2 - 95, this.height / 2 + 60, 190, 20)
                .build());

        this.cooldownDurationButton.active = this.options.messageCoolingDown;
        this.chatQuickMessageButtonWidth.active = this.options.chatQuickMessageButton;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.textField.render(guiGraphics, mouseX, mouseY, delta);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 26, 16777215);
        guiGraphics.drawString(this.font, Component.translatable("quick_chat.config.message")
                        .append(this.textEmpty ? Component.translatable("quick_chat.config.message.empty") : Component.literal("")),
                this.width / 2 - 150, this.height / 2 - 66, this.textEmpty ? Color.RED.getRGB() : 16777215); //如果消息内容为空，那么加入提醒，颜色设置为红色
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String message = this.textField.getValue();
        this.init(minecraft, width, height);
        this.textField.setValue(message);
    }

    @Override
    public void onClose() {
        if (this.minecraft == null) return;
        if (saveText(this.minecraft)) this.minecraft.setScreen(this.parent);
    }

    private void bindButton() {
        if (this.minecraft == null) return;

        //消息内容控制
        this.textField = new EditBox(this.font, this.width / 2 - 150, this.height / 2 - 52, 300, 20, Component.translatable("quick_chat.gui.add_message.title"));
        this.textField.setMaxLength(256);
        this.textField.setValue(this.textEmpty ? "" : this.options.messageValue);
        this.textField.setTooltip(Tooltip.create(Component.translatable("quick_chat.config.message.desc")));

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

        //聊天栏内快捷消息列表
        this.chatQuickMessageButton = getCyclingButtonWidget(this.options.chatQuickMessageButton,
                "quick_chat.config.chat_button",
                "quick_chat.config.chat_button.desc",
                this.width / 2 - 150, this.height / 2,
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
                }).createButton(this.minecraft.options, this.width / 2 + 2, this.height / 2, 148);

        //消息冷却
        this.messageCoolingDownButton = getCyclingButtonWidget(this.options.messageCoolingDown,
                "quick_chat.config.cooldown",
                "quick_chat.config.cooldown.desc",
                this.width / 2 - 150, this.height / 2 + 26,
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
                }).createButton(this.minecraft.options, this.width / 2 + 2, this.height / 2 + 26, 148);
    }

    private CycleButton<Boolean> getCyclingButtonWidget(boolean init, String option, String tooltip, int x, int y, CycleButton.OnValueChange<Boolean> updateCallback) {
        return CycleButton.onOffBuilder(init)
                .withTooltip(value -> Tooltip.create(Component.translatable(tooltip)))
                .create(x, y, 148, 20, Component.translatable(option), updateCallback);
    }

    private boolean saveText(Minecraft client) {
        //切换屏幕之前需要保存文本，如果为空则不允许切换屏幕
        String text = this.textField.getValue();
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