//? if fabric {
package com.movtery.quick_chat.loader.fabric;

import com.movtery.quick_chat.CommonClass;
import com.movtery.quick_chat.platform.Services;
import net.fabricmc.api.ModInitializer;

public class QuickChat implements ModInitializer {

    @Override
    public void onInitialize() {
        Services.init(new FabricPlatformHelper());
        CommonClass.init();
        ModRegister.start();
    }
}
//?}
