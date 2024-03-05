package seng468.scalability.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegisterRequest {
    private String username;
    private String password;
    private String name;

    public RegisterRequest(@JsonProperty("user_name") String username, String password, String name) {
        this.username = username.trim();
        this.password = password.trim();
        this.name = name;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getName() {
        return this.name;
    }
}
