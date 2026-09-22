package com.movtery.quick_chat.core;

public enum WheelDirection {
    UP("up", -90),
    UP_RIGHT("up_right", -45),
    RIGHT("right", 0),
    DOWN_RIGHT("down_right", 45),
    DOWN("down", 90),
    DOWN_LEFT("down_left", 135),
    LEFT("left", 180),
    UP_LEFT("up_left", -135);

    private final String name;
    private final double angle;

    WheelDirection(String name, double angle) {
        this.name = name;
        this.angle = angle;
    }

    public String getTranslateKey() {
        return "quick_chat.gui.wheel.direction." + this.name;
    }

    public double getAngle() {
        return this.angle;
    }

    public static WheelDirection byAngle(double angle) {
        int doubled = (int) Math.round(angle * 2);
        int index = Math.floorMod(doubled - ((int) (UP.angle * 2)) + 45, 720) / 90;
        return values()[index];
    }
}
