package com.wallet.mongo.repository;

import com.wallet.models.entity.WalletTX;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface WalletTXRepository extends MongoRepository<WalletTX, Long> {
    List<WalletTX> findAllByUsername(String username);

    /*
    List<WalletTX> findAllByUsername(String username);
    WalletTX findByWalletTXId(Long walletTXId);
    void deleteByWalletTXId(Long walletTXId);
    void deleteByStockTXId(Long stockTXId);


    default void saveNewWalletTX(WalletTX walletTX) throws Exception {
        if (existsById(walletTX.getWalletTXId())) {
            throw new Exception("Wallet Transaction Already Exists");
        }
        save(walletTX);
    }
    default void saveNewWalletTX(WalletTX walletTX){
        save(walletTX);
    }*/
}
