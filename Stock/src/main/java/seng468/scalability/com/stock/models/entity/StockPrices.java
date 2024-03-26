package seng468.scalability.com.stock.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StockPrices {
    Stock stock;
    Long price;

}
