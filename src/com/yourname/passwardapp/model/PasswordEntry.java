package com.yourname.passwardapp.model;

public class PasswordEntry {
    private String serviceName;
    private String username;
    private String password;

    // コンストラクタの引数のスペルを修正
    public PasswordEntry(String serviceName, String username, String password) {
        this.serviceName = serviceName;
        this.username = username;
        this.password = password; // これで正しく値が代入される
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}