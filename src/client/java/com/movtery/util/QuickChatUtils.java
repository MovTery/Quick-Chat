package com.movtery.util;

import com.movtery.config.Config;
import com.movtery.gui.QuickMessageListScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;

import static com.movtery.QuickChatClient.*;

public class QuickChatUtils {
    private static Config config = null;

    public static void registry() {
        loadConfig(); //加载配置文件

        KeyBinding oneClick = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "quick_chat.keybinding.one_click",
                GLFW.GLFW_KEY_G,
                "quick_chat.name"
        ));

        KeyBinding quickMessage = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "quick_chat.keybinding.quick_message",
                GLFW.GLFW_KEY_H,
                "quick_chat.name"
        ));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("quickchat")
                .then(ClientCommandManager.literal("reload").executes(context -> {
                    loadConfig();
                    context.getSource().sendFeedback(Text.literal("[").append(MODNAME).append("] ").append(Text.translatable("quick_chat.config.reloaded").formatted(Formatting.YELLOW)));
                    return 1;
                }))));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (oneClick.wasPressed()) {
                //开启防误触设置之后，将启用双击检测
                if (getConfig().getOptions().antiFalseContact) {
                    if (isDoubleClick()) break;
                }
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (Objects.isNull(player)) break;

                sendMessage(player);
            }

            while (quickMessage.wasPressed()) {
                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                minecraftClient.setScreen(new QuickMessageListScreen(minecraftClient.currentScreen));
            }
        });
    }

    public static Config getConfig() {
        if (config == null) loadConfig();
        return config;
    }

    private static void loadConfig() {
        String fileName = (MODID + ".json");
        Path configPath = FabricLoader.getInstance().getConfigDir();
        File configFile = new File(configPath + "\\" + fileName);
        if (!configFile.exists()) {
            boolean t;
            try {
                t = configFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (t) LOGGER.info("Created a new configuration file: {}", configFile.getPath());
        }
        config = new Config(configFile);
        config.load();
    }

    public static boolean isDoubleClick() {
        LastMessage instance = LastMessage.getInstance();
        long clickTime = Util.getMeasuringTimeMs();
        //点击即进行判断，如果前后两次点击时间相差不超过0.25秒，那么表示这是一次双击
        boolean isDoubleClick = clickTime - instance.getLastClick() < 250L;
        instance.setLastClick(clickTime);

        return !isDoubleClick;
    }

    public static String getAbbreviatedText(String message, @NotNull MinecraftClient client, int width) {
        if (!(client.textRenderer.getWidth(message) > width)) return message;
        String text = client.textRenderer.trimToWidth(message, width);
        return text.substring(0, text.length() - 3) + "...";
    }

    public static void sendMessage(@NotNull ClientPlayerEntity player) {
        Config.Options options = getConfig().getOptions();

        String message = options.messageValue;
        sendMessage(player, message);
    }

    public static void sendMessage(@NotNull ClientPlayerEntity player, String message) {
        Date date = new Date();
        long timeNum = date.getTime(); //获取当前时间
        long lastTime = LastMessage.getInstance().getLastTime(); //获取上一次发送的时间

        Config.Options options = getConfig().getOptions();
        double duration = Math.abs(options.messageCoolingDuration);
        duration = Math.min(duration, Config.messageCoolingDurationRange[1]);
        duration = Math.max(duration, Config.messageCoolingDurationRange[0]);

        //检查上一次发送消息的时间，如果间隔时间不符合要求则提示过于频繁，不再发送消息
        long differ = timeNum - lastTime; //计算时间差
        if (!options.messageCoolingDown || (lastTime == 0 || differ > 1000 * duration)) {
            if (differ > 500) {
                if (!message.startsWith("/")) {
                    player.networkHandler.sendChatMessage(message);
                } else {
                    player.networkHandler.sendCommand(message.substring(1));
                }
                LastMessage.getInstance().setLastTime(timeNum);
                LastMessage.getInstance().setLastMessage(message);
            }
        } else {
            BigDecimal bigDecimal = BigDecimal.valueOf(duration);
            BigDecimal t = bigDecimal.subtract(BigDecimal.valueOf(differ / 1000.0));
            player.sendMessage(Text.translatable("quick_chat.in_game.too_often").append(String.format(" %.2fs", t)), true);
        }
    }
}
