package com.movtery.quick_chat;

import com.movtery.quick_chat.config.RegisterModsPage;
import com.movtery.quick_chat.gui.QuickMessageListScreen;
import com.movtery.quick_chat.util.QuickChatUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.jarjar.nio.util.Lazy;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforgespi.language.IModInfo;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@Mod(QuickChat.MODID)
public class QuickChat {
    public static final String MODID = "quick_chat";
    public static final Component MODNAME = Component.translatable("quick_chat.name");
    public static final Logger LOGGER = LoggerFactory.getLogger("Quick Chat");
    public static final Lazy<KeyMapping> ONE_CLICK = Lazy.of(() -> new KeyMapping(
            "quick_chat.keybinding.one_click",
            GLFW.GLFW_KEY_G,
            "quick_chat.name"
    ));
    public static final Lazy<KeyMapping> QUICK_MESSAGE = Lazy.of(() -> new KeyMapping(
            "quick_chat.keybinding.quick_message",
            GLFW.GLFW_KEY_H,
            "quick_chat.name"
    ));

    public QuickChat(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);

        for (IModInfo mod : ModList.get().getMods()) {
            if (Objects.equals(mod.getModId(), "yet_another_config_lib_v3")) RegisterModsPage.registerModsPage();
        }

        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (ONE_CLICK.get().consumeClick()) {
                LocalPlayer player = Minecraft.getInstance().player;
                Objects.requireNonNull(player);

                QuickChatUtils.sendMessage(player);
            }
            while (QUICK_MESSAGE.get().consumeClick()) {
                Minecraft minecraft = Minecraft.getInstance();
                minecraft.setScreen(new QuickMessageListScreen(minecraft.screen));
            }
        }
    }

    @SubscribeEvent
    public void registerCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("quickchat")
                .then(Commands.literal("reload")
                        .executes(context -> {
                            QuickChatUtils.getConfig();
                            context.getSource().sendSystemMessage(Component.literal("[").append(MODNAME).append("] ").append(Component.translatable("quick_chat.config.reloaded")).withStyle(ChatFormatting.YELLOW));
                            return 1;
                        })));
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            QuickChatUtils.getConfig();
        }

        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            event.register(ONE_CLICK.get());
            event.register(QUICK_MESSAGE.get());
        }
    }
}