package seng468.scalability.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import seng468.scalability.models.entity.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, String> {
    public Wallet findByUsername(String username);
}