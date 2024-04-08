package com.user.repositories;


import com.user.models.entity.PortfolioEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface PortfolioRepository extends MongoRepository<PortfolioEntry, Long> {
    PortfolioEntry findEntryByStockIdAndUsername(Long id, String username);
    List<PortfolioEntry> findAllByUsername(String username);
    void deleteByStockId(Long stock_id);
}
