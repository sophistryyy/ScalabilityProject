package seng468.scalability.wallet;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, String> {
    public Wallet findByUsername(String username);
}