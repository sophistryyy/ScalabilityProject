package seng468.scalability.models.entity;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "`Wallet Transactions`")
public class WalletTX {
    @Id
    private long walletTXId;
    private String username;
    private long stockTXId;
    private boolean isDebit;
    private int amount;
    private LocalDateTime timestamp;


    public WalletTX(long walletTXId, String username, long stockTXId, boolean isDebit, int amount) {
        this.walletTXId = walletTXId;
        this.username = username;
        this.stockTXId = stockTXId;
        this.isDebit = isDebit;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    public WalletTX() {

    }

    public long getWalletTXId() {
        return this.walletTXId;
    }

    public String getUsername() {
        return this.username;
    }

    public long getStockTXId() {
        return this.stockTXId;
    }

    public boolean getIsDebit() {
        return this.isDebit;
    }

    public int getAmount() {
        return this.amount;
    }

<<<<<<< HEAD
    public String getTimestamp() {
        return this.timestamp;
    }
}
=======
    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }
}
>>>>>>> b0a1ef9 (Wallet stuff from wallet branch)
