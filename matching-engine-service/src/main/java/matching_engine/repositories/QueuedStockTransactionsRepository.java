package matching_engine.repositories;

import java.util.LinkedList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import matching_engine.entity.StockTransaction;
import org.springframework.stereotype.Repository;

@Repository
public interface QueuedStockTransactionsRepository extends JpaRepository<StockTransaction, Long> {

    @Query(value = "SELECT * FROM matching_engine_stock_txs st WHERE st.stock_id = ?1" +
            " AND st.expired = false AND st.is_buy = true AND st.order_status <> 'COMPLETED'" +
            " AND st.order_type <> 'MARKET' ORDER BY st.price ASC NULLS FIRST, st.timestamp ASC", nativeQuery = true)
    LinkedList<StockTransaction> getAllBuyFromStockId(Long stock_id);


    @Query(value = "SELECT * FROM matching_engine_stock_txs st WHERE st.stock_id = ?1" +
            " AND st.expired = false AND st.is_buy = false AND st.order_status <> 'COMPLETED'" +
            " ORDER BY st.price ASC NULLS FIRST, st.timestamp ASC", nativeQuery = true)
    LinkedList<StockTransaction> getAllSellFromStockId(Long stock_id);

    @Query(value = "SELECT * FROM matching_engine_stock_txs st WHERE st.stock_id = ?1" +
            " AND st.expired = false AND st.is_buy = true AND st.order_status <> 'COMPLETED'" +
            " AND st.order_type = 'MARKET' ORDER BY st.timestamp ASC", nativeQuery = true)
    LinkedList<StockTransaction> getAllBuyMarketFromStockId(Long stock_id);



}
