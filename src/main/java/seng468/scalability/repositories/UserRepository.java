package seng468.scalability.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import seng468.scalability.models.Entity.User;

public interface UserRepository extends JpaRepository<User, String> {
    public User findByUsername(String username);
}

