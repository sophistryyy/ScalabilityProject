package seng468.scalability.models.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "`Portfolios`")
public class PortfolioEntry {
    @Id
    @SequenceGenerator(
            name = "portfolios_sequence",
            sequenceName = "portfolios_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "portfolios_sequence"
    )
    private Long portfolioEntryId;
    private Long stockId;
    // username of owner of stock
    private String stockName;
    private String username;
    private Long quantity;
    
    public PortfolioEntry() {}

    public PortfolioEntry(Long stockId, String stockName, String username, Long quantity) {
        this.stockId = stockId;
        this.stockName = stockName;
        this.username = username;
        this.quantity = quantity;
    }

    public Long getPortfolioEntryId() {
        return this.portfolioEntryId;
    }

    public Long getStockId() {
        return this.stockId;
    } 

    public String getStockName() {
        return this.stockName;
    }

    public String getUsername() {
        return this.username;
    }

    public Long getQuantity() {
        return this.quantity;
    }

    public void addQuantity(Long quantityToAdd) {
        quantity += quantityToAdd;
    }

    public void removeQuantity(Long quantityToRemove) throws Exception {
        if(quantityToRemove > this.quantity)
        {
            throw new Exception("User doesn't have enough of stocks' quantity");
        }
        this.quantity -= quantityToRemove;
    }
}
