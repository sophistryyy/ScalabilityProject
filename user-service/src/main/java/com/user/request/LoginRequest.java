package com.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest(@JsonProperty("user_name") String username,@JsonProperty("password") String password) {
        this.username = username.trim();
        this.password = password.trim();
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}
