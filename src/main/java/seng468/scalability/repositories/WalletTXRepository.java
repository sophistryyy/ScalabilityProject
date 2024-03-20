package seng468.scalability.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import seng468.scalability.models.entity.WalletTX;


public interface WalletTXRepository extends JpaRepository<WalletTX, Long> {

    public List<WalletTX> findAllByUsername(String username);

    WalletTX findByWalletTXId(Long walletTXId);
    void deleteByWalletTXId(Long walletTXId);

    void deleteByStockTXId(Long stockTXId);


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
