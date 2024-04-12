package matching_engine.repositories;

import matching_engine.entity.StockEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueuedStockRepository extends JpaRepository<StockEntry, Long> {


}
