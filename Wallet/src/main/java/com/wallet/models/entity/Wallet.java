package com.wallet.models.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;


@Builder

@Entity
public class Wallet {

    @Id
    private String username;

    private Long balance = 0L;

    public Wallet() {}

    public Wallet(String username) {
        this.username = username;
    }

    public Long getBalance() {
        return this.balance;
    }

    public String getUsername() {
        return this.username;
    }

    public void incrementBalance(Long amount) throws Exception { 
        if (amount < 0) {
            throw new Exception("Invalid Amount");
        }
        this.balance = this.balance + amount;
    }

    public void decrementBalance(Long amount) throws Exception {
        if (amount > this.balance) {
            throw new Exception("Insufficient Balance");
        }
        if (amount < 0) {
            throw new Exception("Invalid Amount");
        }

        this.balance = this.balance - amount;
    }
}