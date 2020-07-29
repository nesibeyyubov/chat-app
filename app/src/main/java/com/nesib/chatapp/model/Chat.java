package com.nesib.chatapp.model;

public class Chat {
    private String userName;
    private String profileImage;
    private String lastMessage;
    private long lastMessageDate;
    private String senderId;
    private String receiverId;

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

    public Chat() {
    }

    public Chat(String userName, String profileImage, String lastMessage, long lastMessageDate) {
        this.userName = userName;
        this.profileImage = profileImage;
        this.lastMessage = lastMessage;
        this.lastMessageDate = lastMessageDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(long lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }
}
