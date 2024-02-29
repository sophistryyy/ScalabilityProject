package seng468.scalability.models.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "`Wallet Transactions`")
public class WalletTX {
    @Id
    @SequenceGenerator(
            name = "walletTX_sequence",
            sequenceName = "walletTX_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "walletTX_sequence"
    )
    private Integer walletTXId;
    private String username;
    private Integer stockTXId;

    private boolean isDebit;
    private int amount;
    private LocalDateTime timestamp;



    public WalletTX(String username, Integer stockTXId, boolean isDebit, int amount) {
        this.username = username;
        this.stockTXId = stockTXId;
        this.isDebit = isDebit;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    public WalletTX(Integer walletTXId, String username, Integer stockTXId, boolean isDebit, int amount) {
        this.walletTXId = walletTXId;
        this.username = username;
        this.stockTXId = stockTXId;
        this.isDebit = isDebit;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    public WalletTX() {

    }

    public Integer getWalletTXId() {
        return this.walletTXId;
    }


    public Integer getStockTXId() {
        return this.stockTXId;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean getIsDebit() {
        return this.isDebit;
    }

    public int getAmount() {
        return this.amount;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public void setStockTXId(Integer stockTXId) {
        this.stockTXId = stockTXId;
    }
}
