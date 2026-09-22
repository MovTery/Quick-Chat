package com.movtery.quick_chat.gui;

import com.movtery.quick_chat.Constants;
import com.movtery.quick_chat.core.Config;
import com.movtery.quick_chat.core.Message;
import com.movtery.quick_chat.core.WheelDirection;
import com.movtery.quick_chat.util.QuickChatUtils;
//? if <26.1 {
/*import net.minecraft.client.gui.GuiGraphics;
*///?} else {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?}
//? if >=1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
//?}
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WheelEditorScreen extends Screen {
    private final Screen parent;

    public WheelEditorScreen(Screen parent) {
        super(Component.translatable("quick_chat.gui.wheel.title"));
        this.parent = parent;
    }

    @Override
    public void onClose() {
        if (this.minecraft == null) return;
        QuickChatUtils.openScreen(this.minecraft, this.parent);
    }

    //? if <26.1 {
    /*@Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        //? if <1.20.2 {
        /^this.renderBackground(guiGraphics);
        ^///?}
        super.render(guiGraphics, mouseX, mouseY, delta);

        WheelDirection hovered = hoveredAt(mouseX, mouseY);
        if (this.minecraft != null) {
            WheelRendering.draw(guiGraphics, this.minecraft, this.width / 2.0, this.height / 2.0, hovered);
        }
        showMessageTooltip(guiGraphics, hovered, mouseX, mouseY);
    }
    *///?} else {
    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        WheelDirection hovered = hoveredAt(mouseX, mouseY);
        if (this.minecraft != null) {
            WheelRendering.draw(graphics, this.minecraft, this.width / 2.0, this.height / 2.0, hovered);
        }
        showMessageTooltip(graphics, hovered, mouseX, mouseY);
    }
    //?}

    private void showMessageTooltip(
            //? if <26.1 {
            /*@NotNull GuiGraphics guiGraphics
            *///?} else {
            @NotNull GuiGraphicsExtractor guiGraphics
            //?}
            , @NotNull WheelDirection direction, int mouseX, int mouseY) {
        Message message = Constants.getConfig().getOptions().wheelMessages.get(direction);
        MutableComponent hint = Component.translatable("quick_chat.gui.wheel.click_to_edit").withStyle(ChatFormatting.GRAY);
        Component tooltip = message == null || message.getMessage().isEmpty()
                ? hint
                : QuickChatUtils.getMessageComponent(message).append("\n").append(hint);
        WheelRendering.showTooltip(guiGraphics, tooltip, mouseX, mouseY);
    }

    //? if <1.21.9 {
    /*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && openEditor(mouseX, mouseY)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    *///?} else {
    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubled) {
        if (event.button() == 0 && openEditor(event.x(), event.y())) {
            return true;
        }
        return super.mouseClicked(event, doubled);
    }
    //?}

    private boolean openEditor(double mouseX, double mouseY) {
        if (this.minecraft == null) return false;

        WheelDirection direction = WheelRendering.directionAt(mouseX, mouseY, this.width / 2.0, this.height / 2.0);
        if (direction == null) return false;

        Config.Options options = Constants.getConfig().getOptions();
        Message message = options.wheelMessages.get(direction);
        Component title = Component.translatable("quick_chat.gui.wheel.edit_title", Component.translatable(direction.getTranslateKey()));
        QuickChatUtils.openScreen(this.minecraft, new AddMessageScreen(this, message, (msg, comment) -> {
            options.wheelMessages.put(direction, new Message(msg, comment));
            Constants.getConfig().save();
        }, title));
        return true;
    }

    private @Nullable WheelDirection hoveredAt(double mouseX, double mouseY) {
        return WheelRendering.directionAt(mouseX, mouseY, this.width / 2.0, this.height / 2.0);
    }
}
