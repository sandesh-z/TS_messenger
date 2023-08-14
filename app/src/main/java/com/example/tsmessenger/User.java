package com.example.tsmessenger;

public class User {
    String userId, userName, email, password,lastMessage,status,imageUri;

    public User(String userId, String userName, String email, String password, String lastMessage, String status,String imageUri) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.lastMessage = lastMessage;
        this.status = status;
        this.imageUri = imageUri;
    }
    public User(){

    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
