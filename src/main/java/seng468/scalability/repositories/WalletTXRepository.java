package seng468.scalability.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import seng468.scalability.models.entity.WalletTX;

import java.util.List;

public interface WalletTXRepository extends JpaRepository<WalletTX, Integer> {
    WalletTX findByWalletTXId(Integer walletTXId);
    void deleteByWalletTXId(Integer walletTXId);

    void deleteByStockTXId(Integer stockTXId);

    List<WalletTX> findAllByUsername(String username);
    @Override
    <S extends WalletTX> S save(S entity);

    /*default void saveNewWalletTX(WalletTX walletTX) throws Exception {
        if (existsById(walletTX.getWalletTXId())) {
            throw new Exception("Wallet Transaction Already Exists");
        }
        save(walletTX);
    }*/
    default void saveNewWalletTX(WalletTX walletTX){
        save(walletTX);
    }
}
