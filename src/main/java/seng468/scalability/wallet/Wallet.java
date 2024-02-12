package seng468.scalability.wallet;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import seng468.scalability.authentication.User;

@Entity // This tells Hibernate to make a table out of this class
public class Wallet {

  @Id
  private User name;
  private Integer balance;

  public Integer getBalance() {
    return balance;
  }

  public void incrementBalance(Integer amount) {
    //TODO log the transaction
    this.balance = balance + amount;
  }

}