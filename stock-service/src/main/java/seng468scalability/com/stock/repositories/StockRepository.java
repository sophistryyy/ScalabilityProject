package seng468scalability.com.stock.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import seng468scalability.com.stock.entity.Stock;


public interface StockRepository extends MongoRepository<Stock, Long> {
    public Stock findStockById(Long id);
    public Stock findStockByName(String name);

    public Boolean existsByName(String name);

    public boolean existsById(Long id);

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