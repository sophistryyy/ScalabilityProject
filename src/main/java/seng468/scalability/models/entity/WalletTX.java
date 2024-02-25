package seng468.scalability.models.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "`Wallet Transactions`")
public class WalletTX {
    @Id
    private long walletTXId;
    private long stockTXId;
    private boolean isDebit;
    private int amount;
    private String timestamp;

    public WalletTX(long walletTXId, long stockTXId, boolean isDebit, int amount) {
        this.walletTXId = walletTXId;
        this.stockTXId = stockTXId;
        this.isDebit = isDebit;
        this.amount = amount;
        this.timestamp = new Date().toString();
    }

    public long getWalletTXId() {
        return this.walletTXId;
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

    public String getTimestamp() {
        return this.timestamp;
    }
}
