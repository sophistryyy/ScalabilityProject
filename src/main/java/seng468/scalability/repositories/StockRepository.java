package seng468.scalability.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import seng468.scalability.models.entity.Stock;

public interface StockRepository extends JpaRepository<Stock, Integer> {
    public Stock findStockById(int id);
    public Stock findStockByName(String name);
    public Boolean existsByName(String name);
    public Boolean existsById(int id);

    @Query("SELECT s.name from Stock s where s.id = ?1 LIMIT 1")
    public String findStockNameById(int id);
    @Override
    public <S extends Stock> S save(S entity);

    default void saveNewStock(Stock stock) throws Exception {
        if (existsByName(stock.getName())) {
            throw new Exception("Stock Already Exists");
        }
        this.save(stock);
    }
}