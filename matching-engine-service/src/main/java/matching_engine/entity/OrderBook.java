package matching_engine.entity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import matching_engine.entity.enums.OrderType;
import matching_engine.repositories.QueuedStockRepository;
import matching_engine.repositories.QueuedStockTransactionsRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
//OrderBook consists of StockOrders that beLong to the same stock_id
// it's separated into buy and sell lists where it will later be matched
// Queues are sorted by price, if price is the same, sort by timestamp

@Component
@RequiredArgsConstructor
public class OrderBook {

    private final QueuedStockTransactionsRepository queuedStockTransactionsRepository;
    private final QueuedStockRepository queuedStockRepository;

    private Map<Long, LinkedList<StockTransaction>> buy_orders;
    private Map<Long, LinkedList<StockTransaction>> sell_orders;

    private Map<Long, LinkedList<StockTransaction>> buy_market_orders;

    @PostConstruct
    public void initializeOrderBook(){
        this.buy_orders = new HashMap<>();
        this.sell_orders = new HashMap<>();
        this.buy_market_orders = new HashMap<>();
        List<StockEntry> stocks = queuedStockRepository.findAll();
        if(!stocks.isEmpty()) {
            for (StockEntry stock : stocks) {
                Long stockId = stock.getStock_id();
                LinkedList<StockTransaction> buyStockTransactions = queuedStockTransactionsRepository.getAllBuyFromStockId(stockId);
                LinkedList<StockTransaction> sellStockTransactions = queuedStockTransactionsRepository.getAllSellFromStockId(stockId);
                LinkedList<StockTransaction> buyMarketStockTransactions = queuedStockTransactionsRepository.getAllBuyMarketFromStockId(stockId);
                this.buy_orders.put(stockId, buyStockTransactions);
                this.sell_orders.put(stockId, sellStockTransactions);
                this.buy_market_orders.put(stockId, buyMarketStockTransactions);
            }
        }
        System.out.println("Initialized order book");
        System.out.println(this);
    }

    public boolean isLimitOrdersMatchPossible(Long stock_id)
    {
        StockTransaction buyOrder =  buy_orders.get(stock_id).peek();
        StockTransaction sellOrder = sell_orders.get(stock_id).peek();

        if(buyOrder == null || sellOrder == null){return false;}// one the lists are empty, so can't match


        if(sellOrder.getOrderType() == OrderType.MARKET)
        {
            return true; //seller doesn't care what price it's sold
        }

        return buyOrder.getPrice() >= sellOrder.getPrice(); //can buy at least 1 stock

    }

    public void addStockTransaction(StockTransaction newOrder){
        Long stockId = newOrder.getStock_id();
        Boolean isBuy = newOrder.getIs_buy();
        LinkedList<StockTransaction> modifiedStockTransactions;

        if(isBuy && newOrder.getOrderType() == OrderType.MARKET){
            modifiedStockTransactions = this.buy_market_orders.get(stockId);
        }else if(isBuy && newOrder.getOrderType() == OrderType.LIMIT){
            modifiedStockTransactions = this.buy_orders.get(stockId);
        }else{
            modifiedStockTransactions = this.sell_orders.get(stockId);
        }

        if(modifiedStockTransactions  == null) {
            modifiedStockTransactions = new LinkedList<>();
            modifiedStockTransactions.add(newOrder);
            if(isBuy && newOrder.getOrderType() == OrderType.MARKET){
                this.buy_market_orders.put(stockId, modifiedStockTransactions);
                this.buy_orders.put(stockId, new LinkedList<>());
                this.sell_orders.put(stockId, new LinkedList<>());
            }
            else if(isBuy && newOrder.getOrderType() == OrderType.LIMIT){
                this.buy_market_orders.put(stockId, new LinkedList<>());
                this.buy_orders.put(stockId, modifiedStockTransactions);
                this.sell_orders.put(stockId, new LinkedList<>());
            }else{
                this.buy_market_orders.put(stockId, new LinkedList<>());
                this.buy_orders.put(stockId, new LinkedList<>());
                this.sell_orders.put(stockId,modifiedStockTransactions);
            }
            queuedStockRepository.save(new StockEntry(stockId));
        }else if(isBuy && newOrder.getOrderType() == OrderType.MARKET) {
            this.buy_market_orders.get(stockId).addLast(newOrder);
        }else{
            int index = getRightIndex(modifiedStockTransactions, newOrder);
            modifiedStockTransactions.add(index, newOrder);
        }
        queuedStockTransactionsRepository.save(newOrder);
    }

    public static int getRightIndex(LinkedList<StockTransaction> modifiedStockTransactions, StockTransaction newOrder) {
        Boolean isBuy = newOrder.getIs_buy();

        // Find the right index without sorting
        int index = 0;
        for (StockTransaction existingOrder : modifiedStockTransactions) {
            if(newOrder.getOrderType() == OrderType.MARKET){
                if(existingOrder.getOrderType() == OrderType.LIMIT) {//insert before LIMIT but after existing MARKET orders
                    return index;
                }else{
                    continue;
                }
            }
            if ((isBuy && newOrder.getPrice() > existingOrder.getPrice()) ||
                    (!isBuy && newOrder.getPrice() < existingOrder.getPrice())) {
                return index;
            }
            index++;
        }
        return index;
    }


    public LinkedList<StockTransaction> getBuyOrdersByStockId(Long stockId){
        return this.buy_orders.get(stockId);
    }
    public LinkedList<StockTransaction> getBuyMarketOrdersByStockId(Long stockId){
        return this.buy_market_orders.get(stockId);
    }
    public LinkedList<StockTransaction> getSellOrdersByStockId(Long stockId){
        return this.sell_orders.get(stockId);
    }


    public void popBuyOrder(Long stock_id)
    {
        LinkedList<StockTransaction> buy_orders = this.buy_orders.get(stock_id);
        if(buy_orders != null && !buy_orders.isEmpty())
        {
            buy_orders.remove();
        }
    }
    public void popBuyMarketOrder(Long stock_id)
    {
        LinkedList<StockTransaction> buy_market_orders = this.buy_market_orders.get(stock_id);
        if(buy_market_orders != null && !buy_market_orders.isEmpty())
        {
            buy_market_orders.remove();
        }
    }

    public void popSellOrder(Long stock_id){
        LinkedList<StockTransaction> sell_orders = this.sell_orders.get(stock_id);
        if(sell_orders != null && !sell_orders.isEmpty())
        {
            sell_orders.remove();
        }
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("OrderBook:\n");
        stringBuilder.append("Buy Orders:\n");
        for (Map.Entry<Long, LinkedList<StockTransaction>> entry : buy_orders.entrySet()) {
            stringBuilder.append("Stock ID: ").append(entry.getKey()).append("\n");
            for (StockTransaction transaction : entry.getValue()) {
                stringBuilder.append(transaction).append("\n");
            }
        }
        stringBuilder.append("-----------------\n" +
                "Buy Market Orders:\n");
        for (Map.Entry<Long, LinkedList<StockTransaction>> entry : buy_market_orders.entrySet()) {
            stringBuilder.append("Stock ID: ").append(entry.getKey()).append("\n");
            for (StockTransaction transaction : entry.getValue()) {
                stringBuilder.append(transaction).append("\n");
            }
        }
        stringBuilder.append("-----------------\n" +
                "Sell Orders:\n");
        for (Map.Entry<Long, LinkedList<StockTransaction>> entry : sell_orders.entrySet()) {
            stringBuilder.append("Stock ID: ").append(entry.getKey()).append("\n");
            for (StockTransaction transaction : entry.getValue()) {
                stringBuilder.append(transaction).append("\n");
            }
        }
        return stringBuilder.toString();
    }

}
