package com.movtery.util;

public class LastMessage {
    private static final LastMessage LAST_MESSAGE = new LastMessage();
    private long lastTime = 0;
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

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
