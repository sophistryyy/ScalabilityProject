package seng468.scalability.wallet;

import org.springframework.data.jpa.repository.JpaRepository;
import seng468.scalability.authentication.User;

public interface WalletRepository extends JpaRepository<Wallet, User> {
    public Wallet findByUsername(String username);
}