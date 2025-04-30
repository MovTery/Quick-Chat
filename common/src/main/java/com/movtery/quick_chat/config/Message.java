package com.movtery.quick_chat.config;

import com.google.gson.annotations.Expose;

public class Message {
    @Expose private String message;
    @Expose private String comment;

    public Message(String message, String comment) {
        this.message = message;
        this.comment = comment;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
