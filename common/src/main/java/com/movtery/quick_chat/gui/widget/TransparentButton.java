package com.movtery.quick_chat.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class TransparentButton extends MouseClicksOnlyButton {
    private long lastClickTime = 0;

    public TransparentButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, mutableComponentSupplier -> message.copy());
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        int color;
        if (this.isHovered()) color = packARGB(77, 255, 255, 255);
        else if (!this.active) color = packARGB(200, 0, 0, 0);
        else color = packARGB(128, 0, 0, 0);

        guiGraphics.fill(this.getX(), this.getY() + this.getHeight(), this.getX() + this.getWidth(), this.getY(), color);

        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int textColor = this.active ? 16777215 : 10526880;
        this.renderString(guiGraphics, minecraft.font, textColor | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    private int packARGB(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public boolean notDoubleClick() {
        long clickTime = Util.getMillis();
        boolean isDoubleClick = clickTime - lastClickTime < 250L;
        lastClickTime = clickTime;

        return !isDoubleClick;
    }

    public static class Builder {
        private final Component message;
        private final OnPress onPress;
        @Nullable
        private Tooltip tooltip;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;

        public Builder(Component message, OnPress onPress) {
            this.message = message;
            this.onPress = onPress;
        }

        public Builder position(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder dimensions(int x, int y, int width, int height) {
            return this.position(x, y).size(width, height);
        }

        public Builder tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public TransparentButton build() {
            TransparentButton transparentButton = new TransparentButton(this.x, this.y, this.width, this.height, this.message, this.onPress);
            transparentButton.setTooltip(this.tooltip);
            return transparentButton;
        }
    }
}
