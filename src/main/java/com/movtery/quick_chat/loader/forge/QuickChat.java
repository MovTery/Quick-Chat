//? if forge {
package com.movtery.quick_chat.loader.forge;

import com.movtery.quick_chat.CommonClass;
import com.movtery.quick_chat.Constants;
import com.movtery.quick_chat.platform.Services;
import com.movtery.quick_chat.util.KeybindActions;
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
        Services.init(new ForgePlatformHelper());
        CommonClass.init();
        RegisterModsPage.registerModsPage();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            KeybindActions.handleTick();
        }
    }

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }

        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            event.register(KeybindActions.ONE_CLICK);
            event.register(KeybindActions.QUICK_MESSAGE);
            event.register(KeybindActions.CONFIG);
        }
    }
}
//?}
