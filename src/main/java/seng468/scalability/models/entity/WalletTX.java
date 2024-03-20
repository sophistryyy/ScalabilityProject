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
    private Long walletTXId;
    private String username;
    private Long stockTXId;

    private boolean isDebit;
    private int amount;
    private LocalDateTime timestamp;



    public WalletTX(String username, Long stockTXId, boolean isDebit, int amount) {
        this.username = username;
        this.stockTXId = stockTXId;
        this.isDebit = isDebit;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    public WalletTX(Long walletTXId, String username, Long stockTXId, boolean isDebit, int amount) {
        this.walletTXId = walletTXId;
        this.username = username;
        this.stockTXId = stockTXId;
        this.isDebit = isDebit;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    public WalletTX() {

    }

    public Long getWalletTXId() {
        return this.walletTXId;
    }


    public Long getStockTXId() {
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

    public void setStockTXId(Long stockTXId) {
        this.stockTXId = stockTXId;
    }
}
