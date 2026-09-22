package com.movtery.quick_chat.gui;

import com.movtery.quick_chat.Constants;
import com.movtery.quick_chat.core.Message;
import com.movtery.quick_chat.core.WheelDirection;
import com.movtery.quick_chat.util.KeybindActions;
import com.movtery.quick_chat.util.QuickChatUtils;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.screens.Screen;
//? if <26.1 {
/*import net.minecraft.client.gui.GuiGraphics;
*///?} else {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?}
//? if >=1.21.9 {
import net.minecraft.client.input.KeyEvent;
//?}
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WheelScreen extends Screen {
    public WheelScreen() {
        super(Component.translatable("quick_chat.gui.wheel.title"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        //窗口失焦时松键事件会丢失，静默取消以避免恢复焦点后误发消息
        if (this.minecraft != null && !this.minecraft.isWindowActive()) {
            this.onClose();
        }
    }

    //? if <26.1 {
    /*@Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (this.minecraft == null) return;
        WheelDirection hovered = hoveredAt(mouseX, mouseY);
        WheelRendering.draw(guiGraphics, this.minecraft, this.width / 2.0, this.height / 2.0, hovered);
        drawTitle(guiGraphics);
        showMessageTooltip(guiGraphics, hovered, mouseX, mouseY);
    }
    *///?} else {
    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (this.minecraft == null) return;
        WheelDirection hovered = hoveredAt(mouseX, mouseY);
        WheelRendering.draw(graphics, this.minecraft, this.width / 2.0, this.height / 2.0, hovered);
        drawTitle(graphics);
        showMessageTooltip(graphics, hovered, mouseX, mouseY);
    }
    //?}

    private void drawTitle(
            //? if <26.1 {
            /*@NotNull GuiGraphics guiGraphics
            *///?} else {
            @NotNull GuiGraphicsExtractor guiGraphics
            //?}
    ) {
        if (this.minecraft == null) return;
        int y = (int) Math.max(2, this.height / 2.0 - WheelRendering.OUTER_RADIUS - this.minecraft.font.lineHeight - 4);
        //? if <26.1 {
        /*guiGraphics.drawCenteredString(this.minecraft.font, this.title, this.width / 2, y, 0xFFFFFFFF);
        *///?} else {
        guiGraphics.centeredText(this.minecraft.font, this.title, this.width / 2, y, 0xFFFFFFFF);
        //?}
    }

    private void showMessageTooltip(
            //? if <26.1 {
            /*@NotNull GuiGraphics guiGraphics
            *///?} else {
            @NotNull GuiGraphicsExtractor guiGraphics
            //?}
            , @Nullable WheelDirection direction, int mouseX, int mouseY) {
        if (direction == null) return;
        Message message = Constants.getConfig().getOptions().wheelMessages.get(direction);
        if (message != null) {
            WheelRendering.showMessageTooltip(guiGraphics, message, mouseX, mouseY);
        }
    }

    //? if <1.21.9 {
    /*@Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (KeybindActions.WHEEL.matches(keyCode, scanCode)) {
            settle();
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }
    *///?} else {
    @Override
    public boolean keyReleased(KeyEvent event) {
        if (KeybindActions.WHEEL.matches(event)) {
            settle();
        }
        return super.keyReleased(event);
    }
    //?}

    private @Nullable WheelDirection hoveredAt(int mouseX, int mouseY) {
        return WheelRendering.directionAt(mouseX, mouseY, this.width / 2.0, this.height / 2.0);
    }

    private void settle() {
        if (this.minecraft == null) return;

        Window window = this.minecraft.getWindow();
        double mouseX = this.minecraft.mouseHandler.xpos() * window.getGuiScaledWidth() / (double) window.getScreenWidth();
        double mouseY = this.minecraft.mouseHandler.ypos() * window.getGuiScaledHeight() / (double) window.getScreenHeight();

        WheelDirection direction = WheelRendering.directionAt(mouseX, mouseY, this.width / 2.0, this.height / 2.0);
        if (direction != null) {
            Message message = Constants.getConfig().getOptions().wheelMessages.get(direction);
            if (message != null && !message.getMessage().isEmpty()) {
                QuickChatUtils.sendMessage(this.minecraft, message.getMessage());
            }
        }

        this.onClose();
    }
}
