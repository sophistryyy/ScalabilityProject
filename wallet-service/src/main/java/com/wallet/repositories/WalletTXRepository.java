package com.wallet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.wallet.models.entity.WalletTX;

import java.util.List;


public interface WalletTXRepository extends JpaRepository<WalletTX, Long> {

    List<WalletTX> findAllByUsername(String username);
    WalletTX findByWalletTXId(Long walletTXId);
    void deleteByWalletTXId(Long walletTXId);
    void deleteByStockTXId(Long stockTXId);


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
