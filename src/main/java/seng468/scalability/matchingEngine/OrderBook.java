package seng468.scalability.matchingEngine;
import seng468.scalability.models.entity.StockOrder;

import java.util.LinkedList;
//OrderBook consists of StockOrders that beLong to the same stock_id
// it's separated into buy and sell lists where it will later be matched
// Queues are sorted by price, if price is the same, sort by timestamp

public class OrderBook {

    private final LinkedList<StockOrder> buy_orders;
    private final LinkedList<StockOrder> sell_orders;

    public OrderBook(LinkedList<StockOrder> lstOfSellStocks, LinkedList<StockOrder> lstOfBuyStocks) {
        this.buy_orders = lstOfBuyStocks;
        this.sell_orders = lstOfSellStocks;

    }
    public boolean isMatchPossible()
    {
        StockOrder buyOrder =  buy_orders.peek();
        StockOrder sellOrder = sell_orders.peek();

        if(buyOrder == null || sellOrder == null){return false;}// one the lists are empty, so can't match

        if(buyOrder.getOrderType() == StockOrder.OrderType.LIMIT ) //error handling, don't want to match null price
        {
            if(sellOrder.getOrderType() == StockOrder.OrderType.MARKET)
            {
                return true; //seller doesn't care what price it's sold
            }
            Long sellingPrice = sellOrder.getPrice();
            Long buyingPrice = buyOrder.getPrice();

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
