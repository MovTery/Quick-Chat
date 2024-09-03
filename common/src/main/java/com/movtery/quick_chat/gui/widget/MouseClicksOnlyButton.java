package com.movtery.quick_chat.gui.widget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class MouseClicksOnlyButton extends Button {
    public MouseClicksOnlyButton(int i, int j, int k, int l, Component component, OnPress onPress, CreateNarration createNarration) {
        super(i, j, k, l, component, onPress, createNarration);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean isFocused() {
        return false;
    }
}
