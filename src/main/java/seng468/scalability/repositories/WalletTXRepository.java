package seng468.scalability.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import seng468.scalability.models.entity.WalletTX;

public interface WalletTXRepository extends JpaRepository<WalletTX, Long> {
    public WalletTX findByWalletTXId(long walletTXId);
    public List<WalletTX> findAllByUsername(String username);

    @Override
    public <S extends WalletTX> S save(S entity);

    default void saveNewWalletTX(WalletTX walletTX) throws Exception {
        if (existsById(walletTX.getWalletTXId())) {
            throw new Exception("Wallet Transaction Already Exists");
        }
        this.save(walletTX);
    }
}
