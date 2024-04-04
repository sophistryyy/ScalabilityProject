package com.user.models.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`Portfolios`")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PortfolioEntry {
    @Id
    private Long stockId;
    // username of owner of stock
    private String stockName;
    private String username;
    private Long quantity;


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