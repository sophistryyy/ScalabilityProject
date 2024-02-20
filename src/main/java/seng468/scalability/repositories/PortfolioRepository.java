package seng468.scalability.repositories;

import seng468.scalability.models.Entity.PortfolioEntry;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PortfolioRepository extends JpaRepository<PortfolioEntry, Integer> {
    public PortfolioEntry findEntryByStockIdAndUsername(int id, String username);
}
