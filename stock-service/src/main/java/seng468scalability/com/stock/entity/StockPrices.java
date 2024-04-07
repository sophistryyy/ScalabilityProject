package seng468scalability.com.stock.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Getter
@Document(collection = "stock_prices")
public class StockPrices {
    Stock stock;
    Long price;
}
