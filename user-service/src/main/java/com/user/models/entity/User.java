package com.user.models.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Table(name = "`user`")
@NoArgsConstructor
@Getter
public class User {
    @Id
    private String username;
    private String password;
    private String name;


    public User(String username, String password, String name) {
        this.username = username;
        // Encode password with BCrypt
        this.password = encode(password);
        this.name = name;
    }


    public String encode(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
}