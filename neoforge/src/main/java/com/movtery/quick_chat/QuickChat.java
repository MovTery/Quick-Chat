package com.movtery.quick_chat;

import com.movtery.quick_chat.client.ModKeyMappings;
import com.movtery.quick_chat.client.RegisterModsPage;
import com.movtery.quick_chat.gui.ConfigScreen;
import com.movtery.quick_chat.gui.QuickMessageListScreen;
import com.movtery.quick_chat.util.QuickChatUtils;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;

@Mod(Constants.MOD_ID)
public class QuickChat {

    public QuickChat() {
        // Use NeoForge to bootstrap the Common mod.
        CommonClass.init();
        RegisterModsPage.registerModsPage();
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
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
