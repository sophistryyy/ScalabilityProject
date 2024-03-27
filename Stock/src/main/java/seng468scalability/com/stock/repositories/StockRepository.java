package seng468scalability.com.stock.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import seng468scalability.com.stock.entity.Stock;


public interface StockRepository extends JpaRepository<Stock, Long> {
    public Stock findStockById(Long id);
    public Stock findStockByName(String name);

    public Boolean existsByName(String name);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Stock s WHERE s.id = ?1")
    public boolean existsById(Long id);

    @Query("SELECT s.name from Stock s where s.id = ?1")
    public String findStockNameById(Long id);
    @Override
    public <S extends Stock> S save(S entity);

    default void saveNewStock(Stock stock) throws Exception {
        if (existsByName(stock.getName())) {
            throw new Exception("Stock Already Exists");
        }
        this.save(stock);
    }
}