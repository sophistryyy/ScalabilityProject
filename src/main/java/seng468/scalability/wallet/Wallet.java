package seng468.scalability.wallet;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import seng468.scalability.authentication.User;

@Entity
public class Wallet {

    @Id
    private String username;

    @OneToOne @MapsId
    @JoinColumn(name = "owner_username")
    private User owner;
    private Integer balance;

    public Wallet(String username, Integer balance) {
        // TODO create from either User or username?
        this.username = username;
        this.balance = balance;
    }

    public Integer getBalance() {
        return this.balance;
    }

    public Integer incrementBalance(Integer amount) {
        // TODO log the transaction
        this.balance = this.balance + amount;
        return this.balance;
    }

    public User getUser() {
        return this.owner;
    }

    public void setUser(User owner) {
        this.owner = owner;
    }
}