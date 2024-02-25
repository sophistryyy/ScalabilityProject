package seng468.scalability.models.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Wallet {

    @Id
    private String username;

    private Integer balance = 0;

    public Wallet() {}

    public Wallet(String username) {
        this.username = username;
    }

    public Integer getBalance() {
        return this.balance;
    }

    public void incrementBalance(Integer amount) throws Exception {
        if (amount < 0) {
            throw new Exception("Invalid Amount");
        }

        this.balance = this.balance + amount;
    }

    public void decrementBalance(Integer amount) throws Exception {
        if (amount > balance) {
            throw new Exception("Insufficient Balance");
        }
        if (amount < 0) {
            throw new Exception("Invalid Amount");
        }

        this.balance = this.balance - amount;
    }
}