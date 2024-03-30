package com.user.request;

public class NewWalletRequest {
    private String username;

    public NewWalletRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
