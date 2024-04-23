package com.movtery.quick_chat.util;

import com.movtery.quick_chat.config.Config;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;

import static com.mojang.text2speech.Narrator.LOGGER;
import static com.movtery.quick_chat.QuickChat.MODID;

public class QuickChatUtils {
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

    public static void sendMessage(@NotNull LocalPlayer player) {
        Objects.requireNonNull(player);
        Config.Options options = getConfig().getOptions();

        String message = options.messageValue;
        sendMessage(player, message);
    }

    public static void sendMessage(@NotNull LocalPlayer player, String message) {
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
                    player.connection.sendChat(message);
                } else {
                    player.connection.sendCommand(message.substring(1));
                }
                LastMessage.getInstance().setLastTime(timeNum);
                LastMessage.getInstance().setLastMessage(message);
            }
        } else {
            BigDecimal bigDecimal = BigDecimal.valueOf(duration);
            BigDecimal t = bigDecimal.subtract(BigDecimal.valueOf(differ / 1000.0));
            player.displayClientMessage(Component.translatable("quick_chat.in_game.too_often").append(String.format(" %.2fs", t)), true);
        }
    }
}
