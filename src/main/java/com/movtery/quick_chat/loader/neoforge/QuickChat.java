//? if neoforge {
/*package com.movtery.quick_chat.loader.neoforge;

import com.movtery.quick_chat.CommonClass;
import com.movtery.quick_chat.Constants;
import com.movtery.quick_chat.platform.Services;
import com.movtery.quick_chat.util.KeybindActions;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
//? if <1.20.5 {
/^import net.neoforged.neoforge.event.TickEvent;
^///?} else {
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
//?}

@Mod(Constants.MOD_ID)
public class QuickChat {

    public QuickChat() {
        Services.init(new NeoForgePlatformHelper());
        CommonClass.init();
        RegisterModsPage.registerModsPage();
        NeoForge.EVENT_BUS.register(this);
    }

    //? if <1.20.5 {
    /^@SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            KeybindActions.handleTick();
        }
    }
    ^///?} else {
    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        KeybindActions.handleTick();
    }
    //?}

    //? if <1.20.5 {
    /^@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    ^///?}
    //? if 1.20.6 {
    /^@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    ^///?}
    //? if >=1.21 {
    @EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
    //?}
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }

        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            event.register(KeybindActions.WHEEL);
            event.register(KeybindActions.QUICK_MESSAGE);
            event.register(KeybindActions.CONFIG);
        }
    }
}
*///?}
