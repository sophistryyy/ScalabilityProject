package seng468scalability.com.portfolio.entity;

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
    private PortfolioEntryId portfolioEntryId;
    private String stockName;
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