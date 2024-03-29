package com.wallet.temp;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    public User findByUsername(String username);
}

