package matching_engine.repositories;

import java.util.LinkedList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import matching_engine.entity.StockTransaction;

public interface QueuedStockTransactions extends JpaRepository<StockTransaction, Long> {
    @Query("SELECT st FROM StockTransaction st WHERE st.stock_id = ?1" +
            " AND st.expired = false AND st.is_buy = true ORDER BY st.price ASC NULLS FIRST, st.timestamp ASC")
    LinkedList<StockTransaction> getAllBuyFromStockId(Long stock_id);
}
