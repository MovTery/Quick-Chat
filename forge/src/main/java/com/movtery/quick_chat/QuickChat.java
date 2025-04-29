package com.movtery.quick_chat;

import com.movtery.quick_chat.client.ModKeyMappings;
import com.movtery.quick_chat.client.RegisterModsPage;
import com.movtery.quick_chat.gui.ConfigScreen;
import com.movtery.quick_chat.gui.QuickMessageListScreen;
import com.movtery.quick_chat.util.QuickChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(Constants.MOD_ID)
public final class QuickChat {

    public QuickChat() {
        // Use Forge to bootstrap the Common mod.
        CommonClass.init();
        RegisterModsPage.registerModsPage();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent.Post event) {
        while (ModKeyMappings.ONE_CLICK.get().consumeClick()) {
            //开启防误触设置之后，将启用双击检测
            if (Constants.getConfig().getOptions().antiFalseContact && QuickChatUtils.notDoubleClick()) break;
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

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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
