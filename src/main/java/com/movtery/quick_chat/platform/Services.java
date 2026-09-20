package com.movtery.quick_chat.platform;

import com.movtery.quick_chat.platform.services.IPlatformHelper;

/**
 * 由各加载器入口在初始化时注入平台实现
 */
public class Services {
    public static IPlatformHelper PLATFORM;

    public static void init(IPlatformHelper platform) {
        PLATFORM = platform;
    }
}
