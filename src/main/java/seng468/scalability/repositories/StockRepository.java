package seng468.scalability.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import seng468.scalability.models.entity.Stock;

public interface StockRepository extends JpaRepository<Stock, Integer> {
    public Stock findStockById(int id);
    public Stock findStockByName(String name);
    public Boolean existsByName(String name);
    
    @Override
    public <S extends Stock> S save(S entity);

    default void saveNewStock(Stock stock) throws Exception {
        if (existsByName(stock.getName())) {
            throw new Exception("Stock Already Exists");
        }
        this.save(stock);
    }
}