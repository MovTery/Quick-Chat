package com.movtery.quick_chat.config;

import com.movtery.quick_chat.gui.QuickMessageListScreen;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static com.movtery.quick_chat.util.QuickChatUtils.getConfig;

public class YACLConfig {
    public Screen configScreen(Screen parent) {
        Config.Options defaultVal = new Config.Options();
        Config.Options options = getConfig().getOptions();

        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("quick_chat.name"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("quick_chat.name"))
                        //消息内容控制
                        .option(Option.<String>createBuilder()
                                .name(Component.translatable("quick_chat.config.message"))
                                .description(OptionDescription.of(Component.translatable("quick_chat.config.message.desc")))
                                .binding(defaultVal.messageValue, () -> options.messageValue, newVal -> {
                                    options.messageValue = newVal;
                                    getConfig().save();
                                })
                                .controller(StringControllerBuilder::create)
                                .build())
                        //快捷消息列表
                        .option(ButtonOption.createBuilder()
                                .name(Component.translatable("quick_chat.gui.message_list.title"))
                                .text(Component.translatable("quick_chat.config.open"))
                                .description(OptionDescription.of(Component.translatable("quick_chat.config.message_list.desc")))
                                .action((yaclScreen, buttonOption) -> Minecraft.getInstance().setScreen(new QuickMessageListScreen(YACLConfig.this.configScreen(parent))))
                                .build())
                        //新建组（消息发送冷却）
                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("quick_chat.config.cooldown"))
                                //消息冷却
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("quick_chat.config.cooldown"))
                                        .description(OptionDescription.of(Component.translatable("quick_chat.config.cooldown.desc")))
                                        .binding(defaultVal.messageCoolingDown, () -> options.messageCoolingDown, newVal -> {
                                            options.messageCoolingDown = newVal;
                                            getConfig().save();
                                        })
                                        .controller(BooleanControllerBuilder::create)
                                        .build())
                                //消息冷却时长
                                .option(Option.<Double>createBuilder()
                                        .available(options.messageCoolingDown)
                                        .name(Component.translatable("quick_chat.config.cooldown_duration"))
                                        .description(OptionDescription.of(Component.translatable("quick_chat.config.cooldown_duration.desc")))
                                        .binding(defaultVal.messageCoolingDuration, () -> options.messageCoolingDuration, newVal -> {
                                            options.messageCoolingDuration = newVal;
                                            getConfig().save();
                                        })
                                        .controller(doubleOption -> DoubleSliderControllerBuilder.create(doubleOption)
                                                .range(Config.messageCoolingDurationRange[0], Config.messageCoolingDurationRange[1])
                                                .step(0.5)
                                                .formatValue(value -> Component.literal(value + "s")))
                                        .build())
                                .build())
                        .build())
                .save(() -> Minecraft.getInstance().setScreen(this.configScreen(parent)))
                .build()
                .generateScreen(parent);
    }
}
