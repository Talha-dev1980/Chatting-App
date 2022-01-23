package com.chatting.chatsapp;

public class UserInfo {
    String name,email,status,img;

    public UserInfo() {
    }

    public UserInfo(String name, String email, String status, String img) {
        this.name = name;
        this.email = email;
        this.status = status;
        this.img = img;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
