package com.movtery.quick_chat.neoforge;

import com.movtery.quick_chat.QuickChat;
import com.movtery.quick_chat.gui.ConfigScreen;
import com.movtery.quick_chat.gui.QuickMessageListScreen;
import com.movtery.quick_chat.neoforge.conifg.RegisterModsPage;
import com.movtery.quick_chat.util.QuickChatUtils;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;

import java.io.File;

import static com.movtery.quick_chat.QuickChat.getConfig;

@Mod(QuickChat.MOD_ID)
public final class QuickChatNeoForge {
    public QuickChatNeoForge(IEventBus modEventBus) {
        String fileName = (QuickChat.MOD_ID + ".json");
        File configFolder = new File(FMLLoader.getGamePath().toString(), "config");
        File configFile = new File(configFolder, fileName);
        QuickChat.init(configFile);
        RegisterModsPage.registerModsPage();

        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (ModKeyMappings.ONE_CLICK.get().consumeClick()) {
                //开启防误触设置之后，将启用双击检测
                if (getConfig().getOptions().antiFalseContact && QuickChatUtils.notDoubleClick()) break;
                QuickChatUtils.sendMessage(Minecraft.getInstance());
            }
            while (ModKeyMappings.QUICK_MESSAGE.get().consumeClick()) {
                Minecraft minecraft = Minecraft.getInstance();
                minecraft.setScreen(new QuickMessageListScreen(minecraft.screen));
            }
            while (ModKeyMappings.CONFIG.get().consumeClick()) {
                Minecraft minecraft = Minecraft.getInstance();
                minecraft.setScreen(new ConfigScreen(minecraft.screen));
            }
        }
    }

    @Mod.EventBusSubscriber(modid = QuickChat.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }

        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            event.register(ModKeyMappings.ONE_CLICK.get());
            event.register(ModKeyMappings.QUICK_MESSAGE.get());
            event.register(ModKeyMappings.CONFIG.get());
        }
    }
}
