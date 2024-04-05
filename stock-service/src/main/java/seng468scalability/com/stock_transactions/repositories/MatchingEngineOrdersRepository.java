package seng468scalability.com.stock_transactions.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import seng468scalability.com.stock_transactions.entity.StockTransaction;

import java.util.LinkedList;
import java.util.List;

@Repository
public interface MatchingEngineOrdersRepository extends JpaRepository<StockTransaction, Integer> {


    @Modifying
    @Transactional
    @Query("DELETE FROM StockTransaction so WHERE so.stock_tx_id = ?1")
    void deleteByStock_tx_id(Long stock_tx_id);


    @Query("SELECT so FROM StockTransaction so WHERE so.stock_id = ?1 AND so.orderStatus != seng468.scalability.models.entity.StockOrder$OrderStatus.COMPLETED" +
            " AND so.expired = false AND so.is_buy = false ORDER BY so.price ASC NULLS FIRST, so.timestamp ASC")
    LinkedList<StockTransaction> getAllSellByStock_id(Long stock_id);

    @Query("SELECT so FROM StockTransaction so WHERE so.stock_id = ?1 AND so.orderStatus != seng468.scalability.models.entity.StockOrder$OrderStatus.COMPLETED " +
            "AND so.expired = false AND so.is_buy = true ORDER BY so.price DESC NULLS FIRST, so.timestamp ASC")
    LinkedList<StockTransaction> getAllBuyByStock_id(Long stock_id);

    @Query("SELECT so from StockTransaction so WHERE so.username = ?1")
    List<StockTransaction> findAllByUsername(String username);


    @Query("SELECT so from StockTransaction so where so.stock_tx_id = ?1 AND so.username = ?2")
    StockTransaction findByUsernameAndStockTxId(Long stockTxId, String username);

    //rework this!
    @Query("SELECT so FROM StockTransaction so WHERE so.stock_id = ?1 AND so.orderStatus = seng468.scalability.models.entity.StockOrder$OrderStatus.COMPLETED" +
            " AND so.price is not null AND so.expired = false AND so.is_buy = false ORDER BY so.timestamp DESC")
    LinkedList<StockTransaction> getLastSellOrderByStockId(Long stock_id, Pageable pageable);

    @Query("SELECT so FROM StockTransaction so WHERE so.stock_id = ?1 AND so.orderStatus != seng468.scalability.models.entity.StockOrder$OrderStatus.COMPLETED" +
            " AND so.price is not null AND so.expired = false AND so.is_buy = false ORDER BY so.price DESC")
    LinkedList<StockTransaction> getLowestSellOrderByStockId(Long stock_id, Pageable pageable);


    @Query("SELECT so from StockTransaction so WHERE so.orderType = seng468.scalability.models.entity.StockOrder$OrderType.LIMIT AND " +
            " so.orderStatus != seng468.scalability.models.entity.StockOrder$OrderStatus.COMPLETED  AND so.expired = false ORDER BY so.timestamp ASC")
    LinkedList<StockTransaction> getAllLimitOrders();

    @Query("SELECT so from StockTransaction so WHERE so.parent_stock_tx_id = ?1")
    LinkedList<StockTransaction> findAllParentChildTransactions(Long parentStockTxId);
}
