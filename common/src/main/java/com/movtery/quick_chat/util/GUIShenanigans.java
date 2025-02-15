package com.movtery.quick_chat.util;

import net.minecraft.client.gui.ComponentPath;

public final class GUIShenanigans {
    /**
     * <a href="https://github.com/Aizistral-Studios/No-Chat-Reports/blob/311bb9c/src/main/java/com/aizistral/nochatreports/common/gui/GUIShenanigans.java#L15-L24">From No-Chat-Reports Mod</a>
     */
    public static ComponentPath getLeaf(ComponentPath path) {
        while (path instanceof ComponentPath.Path cpath) {
            if (path != cpath.childPath()) {
                path = cpath.childPath();
            } else {
                break;
            }
        }

        return path;
    }
}
