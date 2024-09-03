package com.movtery.quick_chat.gui.widget;


import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class MouseClicksOnlyButton extends Button {
    protected MouseClicksOnlyButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, CreateNarration pCreateNarration) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pCreateNarration);
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
    public boolean isFocused() {
        return false;
    }
}