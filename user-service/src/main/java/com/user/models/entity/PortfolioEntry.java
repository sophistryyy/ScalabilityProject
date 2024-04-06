package com.user.models.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "portfolio")
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