package seng468.scalability.matchingEngine;
import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import seng468.scalability.models.entity.Stock;
import seng468.scalability.models.entity.StockOrder;
import seng468.scalability.repositories.PortfolioRepository;
import seng468.scalability.repositories.StockRepository;
import seng468.scalability.wallet.WalletRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
//OrderBook consists of StockOrders that belong to the same stock_id
// it's separated into buy and sell lists where it will later be matched
// Queues are sorted by price, if price is the same, sort by timestamp

public class OrderBook {

    private final MatchingEngineOrdersRepository matchingEngineOrdersRepository;
    private final PortfolioRepository portfolioRepository;
    private final WalletRepository walletRepository;
    private final LinkedList<StockOrder> buy_orders;
    private final LinkedList<StockOrder> sell_orders;

    public OrderBook(MatchingEngineOrdersRepository matchingEngineOrdersRepository, PortfolioRepository portfolioRepository, WalletRepository walletRepository,
                     LinkedList<StockOrder> lstOfSellStocks, LinkedList<StockOrder> lstOfBuyStocks) {
        this.matchingEngineOrdersRepository = matchingEngineOrdersRepository;
        this.portfolioRepository =portfolioRepository;
        this.walletRepository = walletRepository;
        this.buy_orders = lstOfBuyStocks;
        this.sell_orders = lstOfSellStocks;

    }
    public boolean isMatchPossible()
    {
        StockOrder buyOrder =  buy_orders.peek();
        StockOrder sellOrder = sell_orders.peek();

        if(buyOrder == null || sellOrder == null){return false;}// one the queues are empty, so can't match

        if(buyOrder.getOrderType() != StockOrder.OrderType.MARKET) //error handling, don't want to match null price
        {
            int sellingPrice = sellOrder.getPrice();
            int buyingPrice = buyOrder.getPrice();

            return buyingPrice >= sellingPrice; //can buy at least 1 stock
        }
        return false; //just in case
    }

    public StockOrder getBuyHead()
    {
        return buy_orders.peek();
    }

    public StockOrder getSellHead()
    {
        return sell_orders.peek();
    }

    public void popBuyOrder()
    {
        if(!buy_orders.isEmpty()){buy_orders.remove();}
    }

    public void popSellOrder()
    {
        if(!sell_orders.isEmpty()){sell_orders.remove();}
    }

    public void addHeadBuy(StockOrder new_order)
    {
        buy_orders.addFirst(new_order);
    }
    public void addHeadSell(StockOrder new_order)
    {
        sell_orders.addFirst(new_order);
    }
    @Override
    public String toString() {
        return "OrderBook{" +
                "buy_orders=" + buy_orders +
                ", sell_orders=" + sell_orders +
                '}';
    }
}
