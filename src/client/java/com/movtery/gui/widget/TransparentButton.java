package com.movtery.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class TransparentButton extends MouseClicksOnlyButton {
    private long lastClickTime = 0;

    public TransparentButton(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, textSupplier -> message.copy());
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        int color;
        if (this.isSelected()) color = packARGB(77, 255, 255, 255);
        else if (!this.active) color = packARGB(200, 0, 0, 0);
        else color = packARGB(128, 0, 0, 0);

        context.fill(this.getX(), this.getY() + this.getHeight(), this.getX() + this.getWidth(), this.getY(), color);

        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.active ? 16777215 : 10526880;
        this.drawMessage(context, minecraftClient.textRenderer, i | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    private int packARGB(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public boolean notDoubleClick() {
        long clickTime = Util.getMeasuringTimeMs();
        boolean isDoubleClick = clickTime - lastClickTime < 250L;
        lastClickTime = clickTime;

        return !isDoubleClick;
    }

    public static class Builder {
        private final Text message;
        private final PressAction onPress;
        @Nullable
        private Tooltip tooltip;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;

        public Builder(Text message, PressAction onPress) {
            this.message = message;
            this.onPress = onPress;
        }

        public TransparentButton.Builder position(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public TransparentButton.Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public TransparentButton.Builder dimensions(int x, int y, int width, int height) {
            return this.position(x, y).size(width, height);
        }

        public TransparentButton.Builder tooltip(@Nullable Tooltip tooltip) {
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
