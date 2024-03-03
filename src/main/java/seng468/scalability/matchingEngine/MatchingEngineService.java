package seng468.scalability.matchingEngine;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import seng468.scalability.matchingEngine.specialReturnClass.IntOrError;
import seng468.scalability.models.entity.*;
import seng468.scalability.models.request.PlaceStockOrderRequest;
import seng468.scalability.repositories.WalletRepository;
import seng468.scalability.repositories.WalletTXRepository;

import java.util.*;

@Service
public class MatchingEngineService {
    private final MatchingEngineOrdersRepository matchingEngineOrdersRepository;
    private final WalletRepository walletRepository;
    private final WalletTXRepository walletTXRepository;
    private final MatchingEngineUtil matchingEngineUtil;

    @Autowired
    public MatchingEngineService(MatchingEngineOrdersRepository repository, WalletTXRepository walletTXRepository,
                                 WalletRepository walletRepository, MatchingEngineUtil matchingEngineUtil){
        this.matchingEngineOrdersRepository = repository;
        this.walletRepository = walletRepository;
        this.walletTXRepository = walletTXRepository;
        this.matchingEngineUtil = matchingEngineUtil;
    }

    @Transactional
    public String placeOrder(PlaceStockOrderRequest req, String username)
    /*
    * If return type is String, error happened, otherwise null means success
    */ {
        String message = matchingEngineUtil.basicVerifier(req);
        if(message != null){return message;}

        StockOrder.OrderType orderType = StockOrder.OrderType.valueOf(req.getOrderType());
        StockOrder order = new StockOrder(req.getStock_id(), req.getIs_buy(), orderType, req.getQuantity(), req.getPrice(), username);
        matchingEngineOrdersRepository.save(order);

        IntOrError verifier = matchingEngineUtil.verifyIfEnough(order);
        if(verifier.getMessage() != null){return verifier.getMessage();}
        order.setWalletTXid(verifier.getWalletTXid());

        int req_stock_id = order.getStockId();
        //at least 1 element in it and no completed transactions
        LinkedList<StockOrder> lstOfSellStocks = matchingEngineOrdersRepository.getAllSellByStock_id(req_stock_id);
        LinkedList<StockOrder> lstOfBuyStocks = matchingEngineOrdersRepository.getAllBuyByStock_id(req_stock_id);
        OrderBook orderBook = new OrderBook(lstOfSellStocks, lstOfBuyStocks);

        try {//just in case
            try_matching(orderBook);
        }catch(Exception e)
        {
            return e.getMessage();
        }
        return null;
    }


    @Transactional
    public void try_matching(OrderBook orderBook) throws Exception
        /*
            * Assumed: if limit order MONEY IS ALREADY DEDUCTED AND USER HAS ENOUGH
            *           MARKET order: money was not deducted!
            * case: user has market order but no money
            * */

    {

        StockOrder buyOrder =  orderBook.getBuyHead();
        StockOrder sellOrder = orderBook.getSellHead();

        if(buyOrder == null || sellOrder == null){return;}

        if(buyOrder.getOrderType() == StockOrder.OrderType.MARKET && sellOrder.getOrderType() == StockOrder.OrderType.MARKET)
        {
            //need to give priorty somehow if both orders are MARKET and price is null
            orderBook.popBuyOrder();
            orderBook.popSellOrder();
            try_matching(orderBook);
            return; //not sure what to do, unhandked case
        }
        if(buyOrder.getOrderType() != StockOrder.OrderType.MARKET) {
            if (!orderBook.isMatchPossible()) {//match isn't possible at all
                return;
            } //buyer's price is guaranteed to be higher if limit
        }
        //if sellingOrder is a MARKET one then set the price to current buyer.
        int sellingPrice = sellOrder.getPrice() != null ? sellOrder.getPrice() : buyOrder.getPrice(); //handle Market orders
        int sellingStocks = sellOrder.getTrueRemainingQuantity();
        //int buyingPrice = buyOrder.getPrice();
        int buyingStocks = buyOrder.getTrueRemainingQuantity();

        Boolean isBuyOrderMarketOne = matchingEngineUtil.isOrderMarketOne(buyOrder);
        Boolean isSellOrderMarketOne = matchingEngineUtil.isOrderMarketOne(sellOrder);
        StockOrder.OrderStatus buyOrderStatus = buyOrder.getOrderStatus();
        StockOrder.OrderStatus sellOrderStatus = sellOrder.getOrderStatus();
        Integer buyOrderStockTXid = buyOrder.getStock_tx_id();
        Integer sellOrderStockTXid = sellOrder.getStock_tx_id();



        Wallet buyerWallet = walletRepository.findByUsername(buyOrder.getUsername());
        Wallet sellerWallet = walletRepository.findByUsername(sellOrder.getUsername());

        //if market order need to check if any user enough money before complete the transaction
        if(isBuyOrderMarketOne) {//then sell is LIMIT

            if (buyerWallet.getBalance() < sellingPrice) //user doesn't have enough even to buy 1
            {                //buy order couldn't be matched, so do the rest
                orderBook.popBuyOrder();
                try_matching(orderBook);
                return;
            }
            //buyer has enough money but maybe not enough to buy all stocks!
            int buyerCanAffordQuantity = (int) Math.floor((double) buyerWallet.getBalance() / sellingPrice);
            if(buyerCanAffordQuantity < buyingStocks)//buyer can afford less than asked
            {
                buyingStocks = buyerCanAffordQuantity;
            }//otherwise buyer can afford all
            int toDeduct = sellingPrice * buyingStocks;
            buyerWallet.decrementBalance(toDeduct); //deduct whole amount even if seller doesn't have enough stocks, refund later

            WalletTX buyerWalletTX = new WalletTX(buyOrder.getUsername(), buyOrderStockTXid, true, toDeduct);
            walletTXRepository.saveNewWalletTX(buyerWalletTX);
            buyOrder.setWalletTXid(buyerWalletTX.getWalletTXId());
        }


        if(buyingStocks > sellingStocks) // user wants to buy more than top seller has
        {
            /*
                SELL is completed now, buyer is getting sellingStocks quantity with price of sellingPrice
                If partially fulfilled then assign a new child stock TX with new wallet TX assigned to it. Original/current order is completed
                If MARKET/LIMIT order with "IN PROGRESS" make a new wallet transaction assigned to original/current order
             */
            //create wallet transaction
            WalletTX sellerWalletTX = new WalletTX(sellOrder.getUsername(),sellOrder.getStock_tx_id(), false, sellingPrice*sellingStocks);
            walletTXRepository.saveNewWalletTX(sellerWalletTX);
            if(sellOrderStatus == StockOrder.OrderStatus.PARTIAL_FULFILLED) {
                //create a child transaction since with how much was purchased, since sell is PARTIAL then it must have child transactions already
                Integer newStockTxId = matchingEngineUtil.createStockTransaction(sellOrder, sellingStocks, sellingPrice, sellerWalletTX.getWalletTXId(), buyOrder.getOrderType());//create stock transaction
                sellerWalletTX.setStockTXId(newStockTxId);//assign actual stock transaction to a new wallet transaction
            }else//in progress MARKET/LIMIT order, no walletTX was assigned
            {
                sellOrder.setWalletTXid(sellerWalletTX.getWalletTXId());
            }
            sellOrder.setOrderStatus(StockOrder.OrderStatus.COMPLETED);//set previously PARTIAL FULFILLED or IN_PROGRESS to completed
            sellOrder.setTrueRemainingQuantity(0); // no more needed
            sellerWallet.incrementBalance(sellingPrice * sellingStocks);//add money to seller based on trueRemaining quantity
            orderBook.popSellOrder();

            //BUY is partially completed
            /*
                BUYER already has a wallet TX if LIMIT and if MARKET (the original placed order, not child ones!)
                Since it's PARTIAL_FULFILLED right away then create a child Stock Transaction
             */
            //create wallet transaction for buyer
            WalletTX buyerWalletTX = new WalletTX(buyOrder.getUsername(),buyOrderStockTXid, true, sellingPrice*sellingStocks);
            walletTXRepository.saveNewWalletTX(buyerWalletTX);
            if(buyOrder.getOrderStatus() == StockOrder.OrderStatus.IN_PROGRESS)//if it was already partial fulfilled then it's original order and it has a wallet tx
            {
                Integer newStockTxId = matchingEngineUtil.createStockTransaction(buyOrder, sellingStocks, sellingPrice, buyerWalletTX.getWalletTXId(), sellOrder.getOrderType());// create a transaction with price bought for;//create stock transaction
                buyerWalletTX.setStockTXId(newStockTxId);//assign actual stock transaction to a new wallet transaction
            }
            //update status
            buyOrder.setOrderStatus(StockOrder.OrderStatus.PARTIAL_FULFILLED);
            if(isBuyOrderMarketOne)
            {
                //since it's MARKET order then need to refund first
                //then look up
                ;
            }
            buyOrder.setTrueRemainingQuantity(buyOrder.getTrueRemainingQuantity() - sellingStocks);//still more to go
            //add stock to portfolio
            matchingEngineUtil.saveToPortfolio(buyOrder, sellingStocks);
        }
        else if(buyingStocks < sellingStocks)//seller has more than buyer
        {
            //SELL is partially completed

            //add wallet transaction
            WalletTX sellerWalletTX = new WalletTX(sellOrder.getUsername(),sellOrderStockTXid, false, sellingPrice*buyingStocks);
            walletTXRepository.saveNewWalletTX(sellerWalletTX);
            //update status
            sellOrder.setOrderStatus(StockOrder.OrderStatus.PARTIAL_FULFILLED);
            Integer newStockTxId = matchingEngineUtil.createStockTransaction(sellOrder, buyingStocks, sellingPrice, sellerWalletTX.getWalletTXId(), buyOrder.getOrderType());//create stock transaction
            sellerWalletTX.setStockTXId(newStockTxId);//set walletTX to a new child stock transaction
            sellOrder.setTrueRemainingQuantity(sellingStocks - buyingStocks);//still more to go
            sellerWallet.incrementBalance(sellingPrice * buyingStocks);//add money to seller based on trueRemaining quantity

            //BUYER is complete

            WalletTX buyerWalletTX = new WalletTX(buyOrder.getUsername(),buyOrderStockTXid, true, sellingPrice*buyingStocks);
            if(buyOrderStatus == StockOrder.OrderStatus.PARTIAL_FULFILLED) { // any partial completed
                walletTXRepository.saveNewWalletTX(buyerWalletTX);
                Integer newChildStockTxId = matchingEngineUtil.createStockTransaction(buyOrder, buyingStocks, sellingPrice, buyerWalletTX.getWalletTXId(), sellOrder.getOrderType());
                buyerWalletTX.setStockTXId(newChildStockTxId);//assign actual stock transaction to a new wallet transaction
            }
            if(buyOrder.getPrice() == null)// MARKET order
            {
                buyOrder.setPrice(sellingPrice);
            }


            //update status
            buyOrder.setOrderStatus(StockOrder.OrderStatus.COMPLETED);//set previously PARTIAL FULFILLED or IN_PROGRESS to completed (original parent!)
            buyOrder.setTrueRemainingQuantity(0);
            //add stock to portfolio
            matchingEngineUtil.saveToPortfolio(buyOrder, buyingStocks);
            //create wallet transaction
            orderBook.popBuyOrder();
        }
        else{//seller stocks = buyer stocks quantity, no more to match
            //create wallet transaction
            WalletTX sellerWalletTX = new WalletTX(sellOrder.getUsername(),sellOrder.getStock_tx_id(), false, sellingPrice*sellingStocks);
            walletTXRepository.saveNewWalletTX(sellerWalletTX);//db assign an id
            //update status
            if(sellOrder.getOrderStatus() == StockOrder.OrderStatus.PARTIAL_FULFILLED)
            {
                Integer newStockTxId = matchingEngineUtil.createStockTransaction(sellOrder, sellingStocks, sellingPrice, sellerWalletTX.getWalletTXId(), buyOrder.getOrderType());//create stock transaction
                sellerWalletTX.setStockTXId(newStockTxId);//set walletTX to a new child stock transaction
            }else{// IN_PROGRESS
                sellOrder.setWalletTXid(sellerWalletTX.getWalletTXId());
            }

            sellOrder.setOrderStatus(StockOrder.OrderStatus.COMPLETED);
            sellOrder.setTrueRemainingQuantity(0);
            sellerWallet.incrementBalance(sellingPrice * sellingStocks);//add money to seller based on trueRemaining quantity
            orderBook.popSellOrder();

            //create wallet transaction
            WalletTX buyerWalletTX = new WalletTX(buyOrder.getUsername(),buyOrder.getStock_tx_id(), true, sellingPrice*buyingStocks);
            //update status
            if(buyOrder.getOrderStatus() == StockOrder.OrderStatus.PARTIAL_FULFILLED) {
                walletTXRepository.saveNewWalletTX(buyerWalletTX);
                matchingEngineUtil.createStockTransaction(buyOrder, buyingStocks, sellingPrice, buyerWalletTX.getWalletTXId(), sellOrder.getOrderType());
            }
            if(buyOrder.getPrice() == null) //market orders have price as null
            {
                buyOrder.setPrice(sellingPrice);
            }

            buyOrder.setOrderStatus(StockOrder.OrderStatus.COMPLETED);
            buyOrder.setTrueRemainingQuantity(0);
            //save to portfolio
            matchingEngineUtil.saveToPortfolio(buyOrder, buyingStocks);
            orderBook.popBuyOrder();
        }

        try_matching(orderBook);

    }


}
