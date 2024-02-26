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

    public void incrementBalance(Integer amount) {
        this.balance = this.balance + amount;
    }

    /*
    public void decrementBalance(Integer amount) throws Exception {
        if (amount > balance) {
            throw new Exception("Insufficient Balance");
        }

        this.balance = this.balance - amount;
    }*/

    public void decrementBalance(Integer amount){
        if (amount <= balance) {
            this.balance = this.balance - amount;
        }

    }
}