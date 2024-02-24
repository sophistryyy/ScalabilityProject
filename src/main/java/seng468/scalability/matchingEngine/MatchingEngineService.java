package seng468.scalability.matchingEngine;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import seng468.scalability.models.entity.PortfolioEntry;
import seng468.scalability.models.entity.Stock;
import seng468.scalability.models.entity.StockOrder;
import seng468.scalability.models.request.PlaceStockOrderRequest;
import seng468.scalability.repositories.PortfolioRepository;
import seng468.scalability.repositories.StockRepository;
import seng468.scalability.wallet.Wallet;
import seng468.scalability.wallet.WalletRepository;

import java.util.*;

@Service
public class MatchingEngineService {

    private final MatchingEngineOrdersRepository matchingEngineOrdersRepository;
    private final PortfolioRepository portfolioRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public MatchingEngineService(MatchingEngineOrdersRepository repository, StockRepository stockRepository,
                                 PortfolioRepository portfolioRepository, WalletRepository walletRepository){
        this.matchingEngineOrdersRepository = repository;
        this.portfolioRepository = portfolioRepository;
        this.walletRepository = walletRepository;

    }

    public String placeOrder(PlaceStockOrderRequest req, String username)
    /*
    * If return type is String, error happened, otherwise null means success
    */ {
        String message = basicVerifier(req);
        if(message != null){return message;}

        StockOrder.OrderType orderType = StockOrder.OrderType.valueOf(req.getOrderType());
        StockOrder order = new StockOrder(req.getStock_id(), req.getIs_buy(), orderType, req.getQuantity(), req.getPrice(), username);
        matchingEngineOrdersRepository.save(order);

        int req_stock_id = order.getStockId();
        LinkedList<StockOrder> lstOfSellStocks = matchingEngineOrdersRepository.getAllSellByStock_id(req_stock_id);//at least 1 element in it and no completed transactions
        LinkedList<StockOrder> lstOfBuyStocks = matchingEngineOrdersRepository.getAllBuyByStock_id(req_stock_id);
        OrderBook orderBook = new OrderBook(matchingEngineOrdersRepository, portfolioRepository, walletRepository, lstOfSellStocks, lstOfBuyStocks);

        try_matching(orderBook);
        return matchingEngineOrdersRepository.findAll().toString();//for testing
    }

    public String basicVerifier(PlaceStockOrderRequest req)
    {
        StockOrder.OrderType orderType;
        try {
            orderType = StockOrder.OrderType.valueOf(req.getOrderType());
        } catch (Exception e) {
            return "Incorrect value of order type";
        }

        if (orderType == StockOrder.OrderType.MARKET && req.getPrice() != null) {
            return "MARKET orders can't have price, set it to null.";
        }

        if(req.getQuantity() != null && req.getQuantity() <= 0)
        {
            return "Please set quantity to more than 0";
        }
        return null;
    }
    public void try_matching(OrderBook orderBook)
            //case user has market order but no money
    {
        StockOrder buyOrder =  orderBook.getBuyHead();
        StockOrder sellOrder = orderBook.getSellHead();

        if(buyOrder == null || sellOrder == null){return;}

        if(buyOrder.getOrderType() != StockOrder.OrderType.MARKET){
            if(!orderBook.isMatchPossible()){return;} //buyer's price is guaranteed to be higher

            int sellingPrice = sellOrder.getPrice();
            int sellingStocks = sellOrder.getQuantity();
            int buyingPrice = buyOrder.getPrice();
            int buyingStocks = buyOrder.getQuantity();

            if(buyingStocks > sellingStocks) // user wants to buy more than top seller has
            {
                int money_to_add = sellingPrice * sellingPrice;
                //SELL is completed
                sellOrder.setOrderStatus(StockOrder.OrderStatus.COMPLETED);
                //add money to seller
                orderBook.popSellOrder();

                //BUY is partially completed
                buyOrder.setOrderStatus(StockOrder.OrderStatus.PARTIAL_FULFILLED);
                //add stocks to buyer's portfolio
                StockOrder childStockOrder = buyOrder.createCopy(buyingStocks - sellingStocks);
                matchingEngineOrdersRepository.save(childStockOrder);
                orderBook.popBuyOrder();//remove parent so it doesn't match and transaction can be saved
                orderBook.addHeadBuy(childStockOrder);//add copy of it
            }
            else if(buyingStocks < sellingStocks)//seller has more
            {
                //SELL is partially completed
                sellOrder.setOrderStatus(StockOrder.OrderStatus.PARTIAL_FULFILLED);
                //add money to seller
                orderBook.popSellOrder();
                StockOrder childStockOrder = sellOrder.createCopy(sellingStocks - buyingStocks );
                matchingEngineOrdersRepository.save(childStockOrder);
                orderBook.addHeadSell(childStockOrder);

                // BUY is COMPLETE
                buyOrder.setOrderStatus(StockOrder.OrderStatus.COMPLETED);
                //add stocks to buyer's portfolio
                orderBook.popBuyOrder();
            }
            else{//seller stocks = buyer stocks quantity, no more to match
                sellOrder.setOrderStatus(StockOrder.OrderStatus.COMPLETED);
                //add money to seller
                orderBook.popSellOrder();

                buyOrder.setOrderStatus(StockOrder.OrderStatus.COMPLETED);
                //add stocks to buyer
                orderBook.popBuyOrder();
            }
            try_matching(orderBook);
        }
    }
}
