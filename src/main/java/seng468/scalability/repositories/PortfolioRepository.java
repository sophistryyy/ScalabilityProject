package seng468.scalability.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import seng468.scalability.models.entity.PortfolioEntry;


public interface PortfolioRepository extends JpaRepository<PortfolioEntry, Long> {
    public PortfolioEntry findEntryByStockIdAndUsername(Long stockId, String username);
    public List<PortfolioEntry> findAllByUsername(String username);

    public void deleteByStockId(Long stockId);
}
