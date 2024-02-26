package seng468.scalability.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import seng468.scalability.models.entity.Wallet;
import seng468.scalability.models.entity.WalletTX;

public interface WalletRepository extends JpaRepository<Wallet, String> {
    public Wallet findByUsername(String username);

    @Override
    public <S extends Wallet> S save(S entity);

    default void saveNewWallet(Wallet wallet) throws Exception {
        if (existsById(wallet.getUsername())) {
            throw new Exception("Wallet Transaction Already Exists");
        }
        this.save(wallet);
    }
}