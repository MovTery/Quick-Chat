package com.movtery;

import com.movtery.util.QuickChatUtils;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickChatClient implements ClientModInitializer {
    public static final String MODID = "quick_chat";
    public static final Text MODNAME = Text.translatable("quick_chat.name");
    public static final Logger LOGGER = LoggerFactory.getLogger("Quick Chat");


    @Override
    public void onInitializeClient() {
        QuickChatUtils.registry();
    }
}
