package com.user.repositories;


import com.user.models.entity.PortfolioEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PortfolioRepository extends JpaRepository<PortfolioEntry, Long> {
    public PortfolioEntry findEntryByStockIdAndUsername(Long id, String username);
    public List<PortfolioEntry> findAllByUsername(String username);
    public void deleteByStockId(Long stock_id);
}
