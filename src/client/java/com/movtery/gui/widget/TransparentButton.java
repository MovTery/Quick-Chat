package com.movtery.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class TransparentButton extends MouseClicksOnlyButton {
    public TransparentButton(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, textSupplier -> message.copy());
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.drawTexture(this.active ? new Identifier("quick_chat", "textures/gui/transparent_button.png") : new Identifier("quick_chat", "textures/gui/transparent_button_disabled.png"), this.getX(), this.getY(), 0, this.getHeight(), this.getWidth(), this.getHeight(), 200, 20);
        if (this.isSelected()) {
            //左边界
            context.drawTexture(new Identifier("quick_chat", "textures/gui/transparent_button_highlighted.png"), this.getX(), this.getY(), 0, this.getHeight(), this.getWidth(), this.getHeight(), 200, 20);
            //右边界
            context.drawTexture(new Identifier("quick_chat", "textures/gui/transparent_button_highlighted.png"), this.getX(), this.getY(), -this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), 200, 20);
        }
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.active ? 16777215 : 10526880;
        this.drawMessage(context, minecraftClient.textRenderer, i | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    @Environment(EnvType.CLIENT)
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