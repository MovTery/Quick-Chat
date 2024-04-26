package com.movtery.util;

public class LastMessage {
    private static final LastMessage LAST_MESSAGE = new LastMessage();
    private long lastTime = 0;
    private long lastClick = 0;
    private String lastMessage = "Hello!";

    private LastMessage() {
    }

    public static LastMessage getInstance() {
        return LAST_MESSAGE;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public long getLastClick() {
        return lastClick;
    }

    public void setLastClick(long lastClick) {
        this.lastClick = lastClick;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
