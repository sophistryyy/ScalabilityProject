package com.wallet.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
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
    @Setter
    private Long stockTXId;
    private boolean isDebit;
    private Long amount;
    private LocalDateTime timestamp;

    public WalletTX(String username, Long stockTXId, boolean isDebit, Long amount) {
        this.username = username;
        this.stockTXId = stockTXId;
        this.isDebit = isDebit;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    public WalletTX(Long walletTXId, String username, Long stockTXId, boolean isDebit, Long amount) {
        this.walletTXId = walletTXId;
        this.username = username;
        this.stockTXId = stockTXId;
        this.isDebit = isDebit;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

}
