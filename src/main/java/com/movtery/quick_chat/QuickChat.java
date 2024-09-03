package com.movtery.quick_chat;

import com.movtery.quick_chat.config.Config;
import com.movtery.quick_chat.config.RegisterModsPage;
import com.movtery.quick_chat.gui.ConfigScreen;
import com.movtery.quick_chat.gui.QuickMessageListScreen;
import com.movtery.quick_chat.util.QuickChatUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.jarjar.nio.util.Lazy;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Mod(QuickChat.MODID)
public class QuickChat {
    public static final String MODID = "quick_chat";
    public static final Logger LOGGER = LoggerFactory.getLogger("Quick Chat");
    private static Config config = null;

    public static Config getConfig() {
        if (config == null) loadConfig();
        return config;
    }

    private static void loadConfig() {
        String fileName = (MODID + ".json");
        Path configPath = new File(FMLLoader.getGamePath().toString(), "config").toPath();
        File configFile = new File(configPath + "\\" + fileName);
        if (!configFile.exists()) {
            boolean succeed;
            try {
                succeed = configFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (succeed) LOGGER.info("Created a new configuration file: {}", configFile.getPath());
        }
        config = new Config(configFile);
        config.load();
    }

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
    public static final Lazy<KeyMapping> CONFIG = Lazy.of(() -> new KeyMapping(
            "quick_chat.keybinding.config",
            GLFW.GLFW_KEY_J,
            "quick_chat.name"
    ));

    public QuickChat(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);

        RegisterModsPage.registerModsPage();

        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (ONE_CLICK.get().consumeClick()) {
                //开启防误触设置之后，将启用双击检测
                if (getConfig().getOptions().antiFalseContact && QuickChatUtils.notDoubleClick()) break;
                QuickChatUtils.sendMessage(Minecraft.getInstance());
            }
            while (QUICK_MESSAGE.get().consumeClick()) {
                Minecraft minecraft = Minecraft.getInstance();
                minecraft.setScreen(new QuickMessageListScreen(minecraft.screen));
            }
            while (CONFIG.get().consumeClick()) {
                Minecraft minecraft = Minecraft.getInstance();
                minecraft.setScreen(new ConfigScreen(minecraft.screen));
            }
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            getConfig();
        }

        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            event.register(ONE_CLICK.get());
            event.register(QUICK_MESSAGE.get());
            event.register(CONFIG.get());
        }
    }
}
