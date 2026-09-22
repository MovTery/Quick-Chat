package com.movtery.quick_chat.gui;

import com.movtery.quick_chat.Constants;
import com.movtery.quick_chat.core.Config;
import com.movtery.quick_chat.core.Message;
import com.movtery.quick_chat.core.WheelDirection;
import com.movtery.quick_chat.util.QuickChatUtils;
import net.minecraft.client.Minecraft;
//? if <26.1 {
/*import net.minecraft.client.gui.GuiGraphics;
*///?} else {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?}
//? if <1.21.6 {
/*import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
*///?}
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class WheelRendering {
    public static final double OUTER_RADIUS = 110;
    public static final double INNER_RADIUS = 40;
    private static final double HALF_ANGLE = Math.PI / 8; //22.5°

    private static final int EMPTY_TEXT_COLOR = 0xFFA0A0A0;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int TEXT_MAX_WIDTH = 60;

    private WheelRendering() {
    }

    public static @Nullable WheelDirection directionAt(double mouseX, double mouseY, double centerX, double centerY) {
        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        if (insideOctagon(dx, dy, INNER_RADIUS)) return null;
        return WheelDirection.byAngle(Math.toDegrees(Math.atan2(dy, dx)));
    }

    //? if <1.21.6 {
    /*public static void showTooltip(@NotNull GuiGraphics graphics, @NotNull Component tooltip, int mouseX, int mouseY) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen != null) {
            screen.setTooltipForNextRenderPass(Tooltip.create(tooltip), DefaultTooltipPositioner.INSTANCE, false);
        }
    }
    *///?}
    //? if >=1.21.6 && <26.1 {
    /*public static void showTooltip(@NotNull GuiGraphics graphics, @NotNull Component tooltip, int mouseX, int mouseY) {
        graphics.setTooltipForNextFrame(tooltip, mouseX, mouseY);
    }
    *///?}
    //? if >=26.1 {
    public static void showTooltip(@NotNull GuiGraphicsExtractor graphics, @NotNull Component tooltip, int mouseX, int mouseY) {
        graphics.setTooltipForNextFrame(tooltip, mouseX, mouseY);
    }
    //?}

    //? if <1.21.6 {
    /*public static void showMessageTooltip(@NotNull GuiGraphics graphics, @NotNull Message message, int mouseX, int mouseY) {
        if (!message.getMessage().isEmpty()) {
            showTooltip(graphics, QuickChatUtils.getMessageComponent(message), mouseX, mouseY);
        }
    }
    *///?}
    //? if >=1.21.6 && <26.1 {
    /*public static void showMessageTooltip(@NotNull GuiGraphics graphics, @NotNull Message message, int mouseX, int mouseY) {
        if (!message.getMessage().isEmpty()) {
            showTooltip(graphics, QuickChatUtils.getMessageComponent(message), mouseX, mouseY);
        }
    }
    *///?}
    //? if >=26.1 {
    public static void showMessageTooltip(@NotNull GuiGraphicsExtractor graphics, @NotNull Message message, int mouseX, int mouseY) {
        if (!message.getMessage().isEmpty()) {
            showTooltip(graphics, QuickChatUtils.getMessageComponent(message), mouseX, mouseY);
        }
    }
    //?}

    //? if <26.1 {
    /*public static void draw(@NotNull GuiGraphics graphics, @NotNull Minecraft minecraft, double centerX, double centerY, @Nullable WheelDirection hovered) {
    *///?} else {
    public static void draw(@NotNull GuiGraphicsExtractor graphics, @NotNull Minecraft minecraft, double centerX, double centerY, @Nullable WheelDirection hovered) {
    //?}
        int alpha = (int) (0.5 * 255);
        int baseColor = packARGB(alpha, 0, 0, 0);
        int hoverColor = packARGB(alpha, 255, 255, 255);

        int yFrom = (int) Math.floor(centerY - OUTER_RADIUS);
        int yTo = (int) Math.ceil(centerY + OUTER_RADIUS);
        for (int y = yFrom; y <= yTo; y++) {
            double rowY = y + 0.5 - centerY;
            double[] outer = octagonRowInterval(rowY, OUTER_RADIUS);
            if (outer == null) continue;
            double[] inner = octagonRowInterval(rowY, INNER_RADIUS);
            for (double[] span : subtractInterval(outer, inner)) {
                graphics.fill((int) Math.round(centerX + span[0]), y, (int) Math.round(centerX + span[1]), y + 1, baseColor);
                if (hovered != null) {
                    double[] wedge = wedgeRowInterval(rowY, hovered);
                    if (wedge != null) {
                        double lo = Math.max(span[0], wedge[0]);
                        double hi = Math.min(span[1], wedge[1]);
                        if (lo < hi) {
                            graphics.fill((int) Math.round(centerX + lo), y, (int) Math.round(centerX + hi), y + 1, hoverColor);
                        }
                    }
                }
            }
        }

        drawTexts(graphics, minecraft, centerX, centerY);
    }

    private static void drawTexts(
            //? if <26.1 {
            /*@NotNull GuiGraphics graphics,
            *///?} else {
            @NotNull GuiGraphicsExtractor graphics,
            //?}
            @NotNull Minecraft minecraft, double centerX, double centerY) {
        Config.Options options = Constants.getConfig().getOptions();
        double textRadius = (apothem(INNER_RADIUS) + apothem(OUTER_RADIUS)) / 2;

        for (WheelDirection direction : WheelDirection.values()) {
            double angle = Math.toRadians(direction.getAngle());
            int x = (int) Math.round(centerX + Math.cos(angle) * textRadius);
            int y = (int) Math.round(centerY + Math.sin(angle) * textRadius - minecraft.font.lineHeight / 2.0 + 1);

            Message message = options.wheelMessages.get(direction);
            boolean empty = message == null || message.getMessage().isEmpty();
            String text;
            if (empty) {
                text = Component.translatable("quick_chat.gui.wheel.empty").getString();
            } else {
                String display = options.displayAsComment && !message.getComment().isEmpty()
                        ? message.getComment() : message.getMessage();
                text = QuickChatUtils.getAbbreviatedText(display, minecraft, TEXT_MAX_WIDTH);
            }
            int color = empty ? EMPTY_TEXT_COLOR : TEXT_COLOR;

            //? if <26.1 {
            /*graphics.drawCenteredString(minecraft.font, text, x, y, color);
            *///?} else {
            graphics.centeredText(minecraft.font, text, x, y, color);
            //?}
        }
    }

    /**
     * 判断中心相对坐标是否位于给定外接圆半径的正八边形内
     */
    private static boolean insideOctagon(double dx, double dy, double radius) {
        double apothem = apothem(radius);
        for (WheelDirection direction : WheelDirection.values()) {
            double angle = Math.toRadians(direction.getAngle());
            if (dx * Math.cos(angle) + dy * Math.sin(angle) > apothem) return false;
        }
        return true;
    }

    /**
     * 水平线（距中心 rowY 处）与正八边形的交集区间，以相对中心的 x 坐标表示，无交集返回 null
     */
    private static double @Nullable [] octagonRowInterval(double rowY, double radius) {
        double apothem = apothem(radius);
        double lo = -radius;
        double hi = radius;
        for (WheelDirection direction : WheelDirection.values()) {
            double angle = Math.toRadians(direction.getAngle());
            double nx = Math.cos(angle);
            double ny = Math.sin(angle);
            if (Math.abs(nx) < 1.0E-9) {
                //上/下方向法线垂直于 x 轴，只约束行是否在八边形高度范围内
                if (ny * rowY > apothem) return null;
                continue;
            }
            double bound = (apothem - ny * rowY) / nx;
            if (nx > 0) {
                hi = Math.min(hi, bound);
            } else {
                lo = Math.max(lo, bound);
            }
        }
        return lo < hi ? new double[]{lo, hi} : null;
    }

    /**
     * 水平线（距中心 rowY 处）与方向楔形（以中心为顶点、张角 45°、向外无限延伸）的交集区间，
     * 以相对中心的 x 坐标表示，无交集返回 null
     */
    private static double @Nullable [] wedgeRowInterval(double rowY, WheelDirection direction) {
        double tangent = Math.tan(HALF_ANGLE);
        double angle = Math.toRadians(direction.getAngle());
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double[] interval = {-Double.MAX_VALUE, Double.MAX_VALUE};

        //楔形即三组半平面约束的交集：法向分量 u = dx·cos + dy·sin >= 0，切向分量 |v| <= u·tan(22.5°)
        if (!applyConstraint(-cos, rowY * sin, interval)) return null;
        if (!applyConstraint(-sin - tangent * cos, rowY * (tangent * sin - cos), interval)) return null;
        if (!applyConstraint(sin - tangent * cos, rowY * (tangent * sin + cos), interval)) return null;
        return interval[0] < interval[1] ? interval : null;
    }

    /**
     * 将形如 a·dx <= b 的单侧约束合并进区间；对任意 dx 都无解（a 为零且 b 为负）时返回 false
     */
    private static boolean applyConstraint(double a, double b, double[] interval) {
        if (Math.abs(a) < 1.0E-9) return b >= 0;
        if (a > 0) {
            interval[1] = Math.min(interval[1], b / a);
        } else {
            interval[0] = Math.max(interval[0], b / a);
        }
        return true;
    }

    private static double[][] subtractInterval(double[] outer, double @Nullable [] inner) {
        List<double[]> spans = new ArrayList<>(2);
        if (inner == null || inner[1] <= outer[0] || inner[0] >= outer[1]) {
            spans.add(outer);
        } else {
            if (inner[0] > outer[0]) spans.add(new double[]{outer[0], inner[0]});
            if (inner[1] < outer[1]) spans.add(new double[]{inner[1], outer[1]});
        }
        return spans.toArray(new double[0][]);
    }

    private static double apothem(double radius) {
        return radius * Math.cos(HALF_ANGLE);
    }

    private static int packARGB(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
