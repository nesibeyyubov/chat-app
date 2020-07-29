package com.nesib.chatapp.model;

public class Message {
    private String senderId;
    private String receiverId;
    private String message;
    private Long date;

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Message(String senderId, String receiverId, String message, Long date) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.date = date;
    }

    public Message() {
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "{" +
                "senderId:'" + senderId + '\'' +
                ", receiverId:'" + receiverId + '\'' +
                ", message:'" + message + '\'' +
                ", date:" + date +
                '}';
    }
}
