package com.user.repositories;

import com.user.models.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    public User findByUsername(String username);
}

