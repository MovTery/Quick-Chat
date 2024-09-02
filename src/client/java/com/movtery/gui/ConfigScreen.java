package com.movtery.gui;

import com.mojang.serialization.Codec;
import com.movtery.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.awt.*;

import static com.movtery.QuickChatClient.getConfig;
import static net.minecraft.client.option.GameOptions.getGenericValueText;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final Config config = getConfig();
    private final Config.Options options = this.config.getOptions();
    private boolean textEmpty;
    private TextFieldWidget textField;
    private ButtonWidget messageListButton;
    private CyclingButtonWidget<Boolean> antiFalseContactButton, chatQuickMessageButton, messageCoolingDownButton;
    private ClickableWidget cooldownDurationButton, chatQuickMessageButtonWidth;

    public ConfigScreen(Screen parent) {
        super(Text.translatable("quick_chat.name"));
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

        this.addSelectableChild(this.textField);
        this.setInitialFocus(this.textField);

        //按钮
        this.addDrawableChild(this.antiFalseContactButton);
        this.addDrawableChild(this.messageListButton);
        this.addDrawableChild(this.chatQuickMessageButton);
        this.addDrawableChild(this.chatQuickMessageButtonWidth);
        this.addDrawableChild(this.messageCoolingDownButton);
        this.addDrawableChild(this.cooldownDurationButton);

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close())
                .dimensions(this.width / 2 - 95, this.height / 2 + 60, 190, 20)
                .build());

        this.cooldownDurationButton.active = this.options.messageCoolingDown;
        this.chatQuickMessageButtonWidth.active = this.options.chatQuickMessageButton;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.textField.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 26, 16777215);
        context.drawTextWithShadow(this.textRenderer, Text.translatable("quick_chat.config.message")
                        .append(this.textEmpty ? Text.translatable("quick_chat.config.message.empty") : Text.literal("")),
                this.width / 2 - 150, this.height / 2 - 66, this.textEmpty ? Color.RED.getRGB() : 16777215); //如果消息内容为空，那么加入提醒，颜色设置为红色
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.textField.getText();
        this.init(client, width, height);
        this.textField.setText(string);
    }

    @Override
    public void close() {
        if (this.client == null) return;

        if (saveText(this.client)) this.client.setScreen(this.parent);
    }

    private void bindButton() {
        if (this.client == null) return;

        //消息内容控制
        this.textField = new TextFieldWidget(this.textRenderer, this.width / 2 - 150, this.height / 2 - 52, 300, 20, Text.translatable("quick_chat.gui.add_message.title"));
        this.textField.setMaxLength(256);
        this.textField.setText(this.textEmpty ? "" : this.options.messageValue);
        this.textField.setTooltip(Tooltip.of(Text.translatable("quick_chat.config.message.desc")));

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
        this.messageListButton = ButtonWidget.builder(Text.translatable("quick_chat.gui.message_list.title"), button -> {
                    if (this.client == null) return;

                    if (saveText(this.client)) this.client.setScreen(new QuickMessageListScreen(this));
                }).tooltip(Tooltip.of(Text.translatable("quick_chat.config.message_list.desc")))
                .dimensions(this.width / 2 + 2, this.height / 2 - 26, 148, 20)
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
        this.chatQuickMessageButtonWidth = new SimpleOption<>("quick_chat.config.chat_button.width",
                value -> Tooltip.of(Text.translatable("quick_chat.config.chat_button.width.desc")),
                (optionText, value) -> getGenericValueText(optionText, Text.literal(value + "px")),
                new SimpleOption.ValidatingIntSliderCallbacks(60, 200),
                Codec.INT.xmap(aInt -> 60, aInt -> 200),
                this.options.chatQuickMessageButtonWidth,
                aInt -> {
                    this.options.chatQuickMessageButtonWidth = aInt;
                    this.config.save();
                }).createWidget(this.client.options, this.width / 2 + 2, this.height / 2, 148);

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
        this.cooldownDurationButton = new SimpleOption<>("quick_chat.config.cooldown_duration",
                value -> Tooltip.of(Text.translatable("quick_chat.config.cooldown_duration.desc")),
                (optionText, value) -> getGenericValueText(optionText, Text.literal(value + "s")),
                new SimpleOption.ValidatingIntSliderCallbacks(1, 15),
                Codec.INT.xmap(aInt -> 1, aInt -> 15),
                this.options.messageCoolingDuration,
                aInt -> {
                    this.options.messageCoolingDuration = aInt;
                    this.config.save();
                }).createWidget(this.client.options, this.width / 2 + 2, this.height / 2 + 26, 148);
    }

    private CyclingButtonWidget<Boolean> getCyclingButtonWidget(boolean init, String option, String tooltip, int x, int y, CyclingButtonWidget.UpdateCallback<Boolean> updateCallback) {
        return CyclingButtonWidget.onOffBuilder(ScreenTexts.ON, ScreenTexts.OFF)
                .initially(init)
                .tooltip(value -> Tooltip.of(Text.translatable(tooltip)))
                .build(x, y, 148, 20, Text.translatable(option), updateCallback);
    }

    private boolean saveText(MinecraftClient client) {
        //切换屏幕之前需要保存文本，如果为空则不允许切换屏幕
        String text = this.textField.getText();
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