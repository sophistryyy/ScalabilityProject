package seng468.scalability.matchingEngine;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import seng468.scalability.models.entity.Stock;
import  seng468.scalability.models.entity.StockOrder;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchingEngineOrdersRepository extends JpaRepository<StockOrder, Integer> {


    @Query("SELECT so FROM StockOrder so WHERE so.stock_id = ?1 AND so.orderStatus != seng468.scalability.models.entity.StockOrder$OrderStatus.COMPLETED" +
            " AND so.orderStatus != seng468.scalability.models.entity.StockOrder$OrderStatus.EXPIRED AND so.is_buy = false ORDER BY so.price ASC NULLS FIRST, so.timestamp ASC")
    LinkedList<StockOrder> getAllSellByStock_id(int stock_id);

    @Query("SELECT so FROM StockOrder so WHERE so.stock_id = ?1 AND so.orderStatus != seng468.scalability.models.entity.StockOrder$OrderStatus.COMPLETED " +
            "AND so.orderStatus != seng468.scalability.models.entity.StockOrder$OrderStatus.EXPIRED AND so.is_buy = true ORDER BY so.price DESC NULLS FIRST, so.timestamp ASC")
    LinkedList<StockOrder> getAllBuyByStock_id(int stock_id);

    @Query("SELECT so from StockOrder so WHERE so.username = ?1 AND so.orderStatus != seng468.scalability.models.entity.StockOrder$OrderStatus.EXPIRED "
            + "AND so.orderStatus != seng468.scalability.models.entity.StockOrder$OrderStatus.EXPIRED")
    List<StockOrder> findAllByUsername(String username);


}
