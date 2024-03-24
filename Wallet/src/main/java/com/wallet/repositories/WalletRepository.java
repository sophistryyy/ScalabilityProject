package com.wallet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.wallet.models.entity.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, String> {
    public Wallet findByUsername(String username);

    @Override
    public <S extends Wallet> S save(S entity);

    default void saveNewWallet(Wallet wallet) throws Exception {
        if (existsById(wallet.getUsername())) {
            throw new Exception("Wallet for User Already Exists");
        }
        this.save(wallet);
    }
}