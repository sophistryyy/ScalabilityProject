package seng468.scalability.models.entity;


public class StockPrices {
    Stock stock;
    Long price;

    public StockPrices(Stock stock, Long price) {
        this.stock = stock;
        this.price = price;
    }

    public Stock getStock() {
        return stock;
    }

    public Long getPrice() {
        return price;
    }
}
