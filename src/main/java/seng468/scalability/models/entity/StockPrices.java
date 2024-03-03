package seng468.scalability.models.entity;


public class StockPrices {
    Stock stock;
    Integer price;

    public StockPrices(Stock stock, Integer price) {
        this.stock = stock;
        this.price = price;
    }

    public Stock getStock() {
        return stock;
    }

    public Integer getPrice() {
        return price;
    }
}
