package seng468.scalability.matchingEngine;
import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import seng468.scalability.models.entity.Stock;
import seng468.scalability.models.entity.StockOrder;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
//OrderBook consists of StockOrders that belong to the same stock_id
// it's separated into buy and sell lists where it will later be matched
// Queues are sorted by price, if price is the same, sort by timestamp

public class OrderBook {

    private final MatchingEngineOrdersRepository matchingEngineOrdersRepository;
    private final Queue<StockOrder> buy_orders;
    private final Queue<StockOrder> sell_orders;

    public OrderBook(MatchingEngineOrdersRepository matchingEngineOrdersRepository,
                     LinkedList<StockOrder> lstOfSellStocks,
                     LinkedList<StockOrder> lstOfBuyStocks) {
        this.matchingEngineOrdersRepository = matchingEngineOrdersRepository;
        this.buy_orders = lstOfBuyStocks;
        this.sell_orders = lstOfSellStocks;
    }
    public void match()
    {
        StockOrder buyOrder =  buy_orders.peek();
        StockOrder sellOrder = sell_orders.peek();

        if(buyOrder == null || sellOrder == null){return ;}

        if(buyOrder.getOrderType() == StockOrder.OrderType.MARKET)
        {
            //need to check if selling price < user's money
            //if so buy
            //else stop any transactions, because current buy is the highest possible and sell is lowest possible
        }



    }

    @Override
    public String toString() {
        return "OrderBook{" +
                "buy_orders=" + buy_orders +
                ", sell_orders=" + sell_orders +
                '}';
    }
}
