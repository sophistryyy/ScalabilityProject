package seng468scalability.com.stock.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StockPrices {
    Stock stock;
    Long price;
}
