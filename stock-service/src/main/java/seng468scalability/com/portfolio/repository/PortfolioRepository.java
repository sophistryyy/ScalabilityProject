package seng468scalability.com.portfolio.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import seng468scalability.com.portfolio.entity.PortfolioEntry;
import seng468scalability.com.portfolio.entity.PortfolioEntryId;

import java.util.List;


public interface PortfolioRepository extends MongoRepository<PortfolioEntry, PortfolioEntryId> {
    PortfolioEntry findByPortfolioEntryId(PortfolioEntryId id);
    List<PortfolioEntry> findAllByPortfolioEntryId_Username(String username);
    void deleteByPortfolioEntryId_StockId(Long stock_id);
}
