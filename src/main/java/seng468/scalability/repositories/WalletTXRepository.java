package seng468.scalability.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import seng468.scalability.models.entity.WalletTX;

public interface WalletTXRepository extends JpaRepository<WalletTX, Integer> {
    WalletTX findByWalletTXId(long walletTXId);

    @Override
    <S extends WalletTX> S save(S entity);

    default void saveNewWalletTX(WalletTX walletTX) throws Exception {
        if (existsById(walletTX.getWalletTXId())) {
            throw new Exception("Wallet Transaction Already Exists");
        }
        save(walletTX);
    }

    void deleteByWalletTXId(long walletTXId);

    void deleteByStockTXId(long stockTXId);
}
