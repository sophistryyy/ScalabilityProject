package seng468.scalability.models.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "`Portfolios`")
public class PortfolioEntry {
    @Id
    private int stockId;
    // username of owner of stock
    private String stockName;
    private String username;
    private int quantity;
    
    public PortfolioEntry() {}

    public PortfolioEntry(int stockId, String stockName, String username, int quantity) {
        this.stockId = stockId;
        this.stockName = stockName;
        this.username = username;
        this.quantity = quantity;
    }

    public int getStockId() {
        return this.stockId;
    } 

    public String getStockName() {
        return this.stockName;
    }

    public String getUsername() {
        return this.username;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void addQuantity(int quantityToAdd) {
        quantity += quantityToAdd;
    }

    public void removeQuantity(int quantityToRemove) throws Exception {
        if(quantityToRemove > this.quantity)
        {
            throw new Exception("User doesn't have enough of stocks' quantity");
        }
        this.quantity -= quantityToRemove;
    }
}
