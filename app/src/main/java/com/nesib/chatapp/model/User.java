package com.nesib.chatapp.model;

public class User {
    private String id;
    private String fullName;
    private String userName;
    private String email;
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public User() {
    }

    public User(String id, String fullName, String userName, String email,String imageUrl) {
        this.id = id;
        this.fullName = fullName;
        this.userName = userName;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "{" +
                "id:'" + id + '\'' +
                ", fullName:'" + fullName + '\'' +
                ", userName:'" + userName + '\'' +
                ", email:'" + email + '\'' +
                '}';
    }
}
