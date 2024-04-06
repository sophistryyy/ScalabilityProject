package com.user.repositories;


import com.user.models.entity.PortfolioEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface PortfolioRepository extends MongoRepository<PortfolioEntry, Long> {
    public PortfolioEntry findEntryByStockIdAndUsername(Long id, String username);
    public List<PortfolioEntry> findAllByUsername(String username);
    public void deleteByStockId(Long stock_id);
}
