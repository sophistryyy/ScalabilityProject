package matching_engine.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import matching_engine.entity.OrderBook;
import matching_engine.entity.OrderExecutionMessage;
import matching_engine.entity.StockTransaction;
import matching_engine.entity.enums.OrderStatus;
import matching_engine.entity.enums.OrderType;
import matching_engine.requests.AddStockToUserRequest;
import matching_engine.requests.NewStockTransactionRequest;
import matching_engine.requests.NewWalletTransactionRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;

@RequiredArgsConstructor
@Slf4j
@Service
public class MatchingEngineUtil {

    private final RabbitMQProducer producer;
    private final OrderBook orderBook;


    public void receiveNewElement(StockTransaction transaction){
        try {
            orderBook.addStockTransaction(transaction);
            System.out.println("New element added successfully\n" + orderBook);
        }catch (Exception e){
            log.info("Error add stock to orderBook. " + e.getMessage());
            return;
        }
        try {
            match(transaction.getStock_id()
            );
        }catch (Exception e){
            log.info("Error matching stocks. " + e.getMessage());
        }


    }

    public void match(Long stockId) {
        //to do :               1 remove from db once completed 2. refactor code, currently calls match again (too many operations)

        //new element was inserted in correct queue, so now have to match according to stock_id.
        LinkedList<StockTransaction> lstOfSellStocks = orderBook.getSellOrdersByStockId(stockId);
        LinkedList<StockTransaction> lstOfBuyStocks = orderBook.getBuyOrdersByStockId(stockId);
        LinkedList<StockTransaction> lstOfBuyMarketStocks = orderBook.getBuyMarketOrdersByStockId(stockId);
        if(lstOfSellStocks == null || lstOfSellStocks.isEmpty() || (lstOfBuyStocks == null && lstOfBuyMarketStocks == null)){return;}

        if(lstOfBuyMarketStocks != null && !lstOfBuyMarketStocks.isEmpty()){
            //match market orders first
        }


        if(lstOfBuyStocks != null && !lstOfBuyStocks.isEmpty()){
            //match LIMIT buy market with either Market  or Limit sell
            StockTransaction buyOrder =  lstOfBuyStocks.getFirst(); //guaranteed to be LIMIT!
            StockTransaction sellOrder = lstOfSellStocks.getFirst();

            Long sellingPrice = sellOrder.getPrice() != null ? sellOrder.getPrice() : buyOrder.getPrice(); //handle Market Sell orders
            Long sellingStocks = sellOrder.getTrueRemainingQuantity();
            //Long buyingPrice = buyOrder.getPrice();
            Long buyingStocks = buyOrder.getTrueRemainingQuantity();
            OrderStatus buyOrderStatus = buyOrder.getOrderStatus();
            OrderStatus sellOrderStatus = sellOrder.getOrderStatus();
            Boolean isSellOrderMarketOne = (sellOrder.getOrderType() == OrderType.MARKET);

            if (!orderBook.isLimitOrdersMatchPossible(stockId)) {//match isn't possible at all
                return;
            } //buyer's price is guaranteed to be higher if limit. BUY IS POSSIBLE

            NewStockTransactionRequest stockTransactionRequest;
            NewWalletTransactionRequest walletTransactionRequest;
            AddStockToUserRequest addStockToUserRequest;

            if(buyingStocks > sellingStocks) // user wants to buy more than top seller has
            {
            /*
                SELL is completed now, buyer is getting sellingStocks quantity with price of sellingPrice
             */
            if(sellOrderStatus == OrderStatus.PARTIAL_FULFILLED) {
                //create child stock tx
                stockTransactionRequest = createNewChildStockTx(sellOrder,sellingStocks,OrderStatus.COMPLETED,sellingPrice);//stock_tx_id is null!
                //set wallet TX to this child seller tx

            }else{
                //set wallet TX to this origin seller
                stockTransactionRequest = createNewChildStockTx(sellOrder,sellingStocks,OrderStatus.COMPLETED,sellingPrice);
                stockTransactionRequest.setStock_tx_id(sellOrder.getStock_tx_id());
                stockTransactionRequest.setParent_stock_tx_id(null);
            }
            walletTransactionRequest = createNewWalletTx(sellOrder.getUsername(), false, sellingPrice*sellingStocks);
            addStockToUserRequest = createNewAddStockToUserRequest(sellOrder.getUsername(), stockId, sellOrder.getQuantity());
            //set seller to be completed
            //set true remaining q to 0
            sellOrder.setOrderStatus(OrderStatus.COMPLETED);
            sellOrder.setTrueRemainingQuantity(0L);
            orderBook.popSellOrder(stockId);
            producer.sendMessage(new OrderExecutionMessage(stockTransactionRequest, walletTransactionRequest, addStockToUserRequest));
            //add stock to portfolio

            //BUY ORDER
            //create child stock tx
            //create wallet TX and assign to child stock tx
            buyOrder.setOrderStatus(OrderStatus.PARTIAL_FULFILLED);
            buyOrder.setTrueRemainingQuantity(buyingStocks - sellingStocks);//still more to go
            stockTransactionRequest = createNewChildStockTx(buyOrder, sellingStocks,  OrderStatus.COMPLETED, sellingPrice);//stock_tx_id is null!
            walletTransactionRequest = createNewWalletTx(buyOrder.getUsername(), true, sellingPrice*sellingStocks);
            producer.sendMessage(new OrderExecutionMessage(stockTransactionRequest, walletTransactionRequest, null));
        }else if(buyingStocks < sellingStocks){
            //SELL is partially completed
            sellOrder.setOrderStatus(OrderStatus.PARTIAL_FULFILLED);
            sellOrder.setTrueRemainingQuantity(sellingStocks - buyingStocks);//still more to go
            stockTransactionRequest = createNewChildStockTx(sellOrder, buyingStocks,OrderStatus.COMPLETED,sellingPrice);//stock_tx_id is null!
            walletTransactionRequest = createNewWalletTx(sellOrder.getUsername(), false, sellingPrice*buyingStocks);
            addStockToUserRequest = createNewAddStockToUserRequest(sellOrder.getUsername(), stockId, buyOrder.getQuantity());
            producer.sendMessage(new OrderExecutionMessage(stockTransactionRequest, walletTransactionRequest, addStockToUserRequest));


            //Buy is completed
            if(buyOrderStatus == OrderStatus.PARTIAL_FULFILLED) {
                //create child stock tx
                stockTransactionRequest = createNewChildStockTx(buyOrder, buyingStocks,OrderStatus.COMPLETED,sellingPrice);//stock_tx_id is null!
                //set wallet TX to this child seller tx

            }else{
                //set wallet TX to this origin seller
                stockTransactionRequest = createNewChildStockTx(buyOrder, buyingStocks,OrderStatus.COMPLETED,sellingPrice); //pretty sure we have to delete this one
                stockTransactionRequest.setStock_tx_id(buyOrder.getStock_tx_id());
                stockTransactionRequest.setParent_stock_tx_id(null);
            }
            walletTransactionRequest = createNewWalletTx(buyOrder.getUsername(), true, sellingPrice*buyingStocks);
            //set seller to be completed
            //set true remaining q to 0
            buyOrder.setOrderStatus(OrderStatus.COMPLETED);
            buyOrder.setTrueRemainingQuantity(0L);
            orderBook.popBuyOrder(stockId);
            producer.sendMessage(new OrderExecutionMessage(stockTransactionRequest, walletTransactionRequest, null));
            //add stock to portfolio
        }else{
            if(sellOrderStatus == OrderStatus.PARTIAL_FULFILLED) {
                //create child stock tx
                stockTransactionRequest = createNewChildStockTx(sellOrder, sellingStocks, OrderStatus.COMPLETED,sellingPrice);//stock_tx_id is null!

            }else{
                //set wallet TX to this origin seller
                stockTransactionRequest = createNewChildStockTx(sellOrder, sellingStocks,OrderStatus.COMPLETED,sellingPrice);
                stockTransactionRequest.setStock_tx_id(sellOrder.getStock_tx_id());
                stockTransactionRequest.setParent_stock_tx_id(null);
            }
            walletTransactionRequest = createNewWalletTx(sellOrder.getUsername(), false, sellingPrice*sellingStocks);
            addStockToUserRequest = createNewAddStockToUserRequest(sellOrder.getUsername(), stockId, sellOrder.getQuantity());
            //set seller to be completed
            //set true remaining q to 0
            sellOrder.setOrderStatus(OrderStatus.COMPLETED);
            sellOrder.setTrueRemainingQuantity(0L);
            orderBook.popSellOrder(stockId);
            producer.sendMessage(new OrderExecutionMessage(stockTransactionRequest, walletTransactionRequest, addStockToUserRequest));

            //Buy is completed
            if(buyOrderStatus == OrderStatus.PARTIAL_FULFILLED) {
                //create child stock tx
                stockTransactionRequest = createNewChildStockTx(buyOrder,buyingStocks,OrderStatus.COMPLETED,sellingPrice);//stock_tx_id is null!
                //set wallet TX to this child seller tx

            }else{
                //set wallet TX to this origin seller
                stockTransactionRequest = createNewChildStockTx(buyOrder,buyingStocks,OrderStatus.COMPLETED,sellingPrice); //pretty sure we have to delete this one
                stockTransactionRequest.setStock_tx_id(buyOrder.getStock_tx_id());
                stockTransactionRequest.setParent_stock_tx_id(null);
            }
            walletTransactionRequest = createNewWalletTx(buyOrder.getUsername(), true, sellingPrice*buyingStocks);
            //set seller to be completed
            //set true remaining q to 0
            buyOrder.setOrderStatus(OrderStatus.COMPLETED);
            buyOrder.setTrueRemainingQuantity(0L);
            orderBook.popBuyOrder(stockId);
            producer.sendMessage(new OrderExecutionMessage(stockTransactionRequest, walletTransactionRequest, null));
        }
        match(stockId);
        System.out.println("\n" + orderBook.toString() + "\n");
        }

        /*
        StockTransaction buyOrder =  lstOfBuyStocks.getFirst(); //guaranteed to be LIMIT!
        StockTransaction sellOrder = lstOfSellStocks.getFirst();
        if(buyOrder == null || sellOrder == null){return;}


        Long sellingPrice = sellOrder.getPrice() != null ? sellOrder.getPrice() : buyOrder.getPrice(); //handle Market orders
        Long sellingStocks = sellOrder.getTrueRemainingQuantity();
        //Long buyingPrice = buyOrder.getPrice();
        Long buyingStocks = buyOrder.getTrueRemainingQuantity();
        OrderStatus buyOrderStatus = buyOrder.getOrderStatus();
        OrderStatus sellOrderStatus = sellOrder.getOrderStatus();
        Boolean isBuyOrderMarketOne = (buyOrder.getOrderType() == OrderType.MARKET);
        Boolean isSellOrderMarketOne =(sellOrder.getOrderType() == OrderType.MARKET);
        Long buyOrderStockTXid = buyOrder.getStock_tx_id();
        Long sellOrderStockTXid = sellOrder.getStock_tx_id();


        if(buyOrder.getOrderType() == OrderType.LIMIT) {
            if (!orderBook.isLimitOrdersMatchPossible(stockId)) {//match isn't possible at all
                return;
            } //buyer's price is guaranteed to be higher if limit. BUY IS POSSIBLE

        }else{//buy order is Market
            //create wallet tx, and remove money

        }


        NewStockTransactionRequest stockTransactionRequest;
        NewWalletTransactionRequest walletTransactionRequest;
        AddStockToUserRequest addStockToUserRequest;
        if(buyingStocks > sellingStocks) // user wants to buy more than top seller has
        {
            /*
                SELL is completed now, buyer is getting sellingStocks quantity with price of sellingPrice
                If partially fulfilled then assign a new child stock TX with new wallet TX assigned to it. Original/current order is completed(undefined)
                If MARKET/LIMIT order with "IN PROGRESS" make a new wallet transaction assigned to original/current order
             * /
            if(sellOrderStatus == OrderStatus.PARTIAL_FULFILLED) {
                //create child stock tx
                stockTransactionRequest = createNewChildStockTx(sellOrder, buyOrder.getOrderType(),sellingStocks,OrderStatus.COMPLETED,sellingPrice);//stock_tx_id is null!
                //set wallet TX to this child seller tx

            }else{
                //set wallet TX to this origin seller
                stockTransactionRequest = createNewChildStockTx(sellOrder, sellOrder.getOrderType(),sellingStocks,OrderStatus.COMPLETED,sellingPrice);
                stockTransactionRequest.setStock_tx_id(sellOrder.getStock_tx_id());
                stockTransactionRequest.setParent_stock_tx_id(null);
            }
            walletTransactionRequest = createNewWalletTx(sellOrder.getUsername(), false, sellingPrice*sellingStocks);
            addStockToUserRequest = createNewAddStockToUserRequest(sellOrder.getUsername(), stockId, sellOrder.getQuantity());
            //set seller to be completed
            //set true remaining q to 0
            sellOrder.setOrderStatus(OrderStatus.COMPLETED);
            sellOrder.setTrueRemainingQuantity(0L);
            orderBook.popSellOrder(stockId);
            producer.sendMessage(new OrderExecutionMessage(stockTransactionRequest, walletTransactionRequest, addStockToUserRequest));
            //add stock to portfolio

            //BUY ORDER
            //create child stock tx
            //create wallet TX and assign to child stock tx
            buyOrder.setOrderStatus(OrderStatus.PARTIAL_FULFILLED);
            buyOrder.setTrueRemainingQuantity(buyingStocks - sellingStocks);//still more to go
            stockTransactionRequest = createNewChildStockTx(buyOrder, buyOrder.getOrderType(),sellingStocks,OrderStatus.COMPLETED,sellingPrice);//stock_tx_id is null!
            walletTransactionRequest = createNewWalletTx(buyOrder.getUsername(), true, sellingPrice*sellingStocks);
            producer.sendMessage(new OrderExecutionMessage(stockTransactionRequest, walletTransactionRequest, null));
        }else if(buyingStocks < sellingStocks){
            //SELL is partially completed
            sellOrder.setOrderStatus(OrderStatus.PARTIAL_FULFILLED);
            sellOrder.setTrueRemainingQuantity(sellingStocks - buyingStocks);//still more to go
            stockTransactionRequest = createNewChildStockTx(sellOrder, buyOrder.getOrderType(),buyingStocks,OrderStatus.COMPLETED,sellingPrice);//stock_tx_id is null!
            walletTransactionRequest = createNewWalletTx(sellOrder.getUsername(), false, sellingPrice*buyingStocks);
            addStockToUserRequest = createNewAddStockToUserRequest(sellOrder.getUsername(), stockId, buyOrder.getQuantity());
            producer.sendMessage(new OrderExecutionMessage(stockTransactionRequest, walletTransactionRequest, addStockToUserRequest));


            //Buy is completed
            if(buyOrderStatus == OrderStatus.PARTIAL_FULFILLED) {
                //create child stock tx
                stockTransactionRequest = createNewChildStockTx(buyOrder, buyOrder.getOrderType(),buyingStocks,OrderStatus.COMPLETED,sellingPrice);//stock_tx_id is null!
                //set wallet TX to this child seller tx

            }else{
                //set wallet TX to this origin seller
                stockTransactionRequest = createNewChildStockTx(buyOrder, buyOrder.getOrderType(),buyingStocks,OrderStatus.COMPLETED,sellingPrice); //pretty sure we have to delete this one
                stockTransactionRequest.setStock_tx_id(buyOrder.getStock_tx_id());
                stockTransactionRequest.setParent_stock_tx_id(null);
            }
            walletTransactionRequest = createNewWalletTx(buyOrder.getUsername(), true, sellingPrice*buyingStocks);
            //set seller to be completed
            //set true remaining q to 0
            buyOrder.setOrderStatus(OrderStatus.COMPLETED);
            buyOrder.setTrueRemainingQuantity(0L);
            orderBook.popBuyOrder(stockId);
            producer.sendMessage(new OrderExecutionMessage(stockTransactionRequest, walletTransactionRequest, null));
            //add stock to portfolio
        }else{
            if(sellOrderStatus == OrderStatus.PARTIAL_FULFILLED) {
                //create child stock tx
                stockTransactionRequest = createNewChildStockTx(sellOrder, buyOrder.getOrderType(),sellingStocks,OrderStatus.COMPLETED,sellingPrice);//stock_tx_id is null!
                //set wallet TX to this child seller tx

            }else{
                //set wallet TX to this origin seller
                stockTransactionRequest = createNewChildStockTx(sellOrder, sellOrder.getOrderType(),sellingStocks,OrderStatus.COMPLETED,sellingPrice);
                stockTransactionRequest.setStock_tx_id(sellOrder.getStock_tx_id());
                stockTransactionRequest.setParent_stock_tx_id(null);
            }
            walletTransactionRequest = createNewWalletTx(sellOrder.getUsername(), false, sellingPrice*sellingStocks);
            addStockToUserRequest = createNewAddStockToUserRequest(sellOrder.getUsername(), stockId, sellOrder.getQuantity());
            //set seller to be completed
            //set true remaining q to 0
            sellOrder.setOrderStatus(OrderStatus.COMPLETED);
            sellOrder.setTrueRemainingQuantity(0L);
            orderBook.popSellOrder(stockId);
            producer.sendMessage(new OrderExecutionMessage(stockTransactionRequest, walletTransactionRequest, addStockToUserRequest));

            //Buy is completed
            if(buyOrderStatus == OrderStatus.PARTIAL_FULFILLED) {
                //create child stock tx
                stockTransactionRequest = createNewChildStockTx(buyOrder, buyOrder.getOrderType(),buyingStocks,OrderStatus.COMPLETED,sellingPrice);//stock_tx_id is null!
                //set wallet TX to this child seller tx

            }else{
                //set wallet TX to this origin seller
                stockTransactionRequest = createNewChildStockTx(buyOrder, buyOrder.getOrderType(),buyingStocks,OrderStatus.COMPLETED,sellingPrice); //pretty sure we have to delete this one
                stockTransactionRequest.setStock_tx_id(buyOrder.getStock_tx_id());
                stockTransactionRequest.setParent_stock_tx_id(null);
            }
            walletTransactionRequest = createNewWalletTx(buyOrder.getUsername(), true, sellingPrice*buyingStocks);
            //set seller to be completed
            //set true remaining q to 0
            buyOrder.setOrderStatus(OrderStatus.COMPLETED);
            buyOrder.setTrueRemainingQuantity(0L);
            orderBook.popBuyOrder(stockId);
            producer.sendMessage(new OrderExecutionMessage(stockTransactionRequest, walletTransactionRequest, null));
        }
        match(stockId);
        System.out.println("\n" + orderBook.toString() + "\n");
        */
    }

    private AddStockToUserRequest createNewAddStockToUserRequest(String username, Long stockId, Long quantity) {
        return new AddStockToUserRequest(username, stockId, quantity);
    }

    public NewStockTransactionRequest createNewChildStockTx(StockTransaction order, Long newQuantity, OrderStatus newOrderStatus, Long newPrice){
        Long parent_stock_tx_id = order.getParent_stock_tx_id() == null ? order.getStock_tx_id()
                : order.getParent_stock_tx_id(); //reference true parent

        return NewStockTransactionRequest.builder().stockId(order.getStock_id()).
                isBuy(order.getIs_buy())
                .orderType(order.getOrderType())
                .quantity(newQuantity).price(newPrice)
                .orderStatus(newOrderStatus)
                .username(order.getUsername())
                .parent_stock_tx_id(parent_stock_tx_id)
                .walletTXid(null).stock_tx_id(null).build();
    }

    public NewWalletTransactionRequest createNewWalletTx(String username, boolean isDebit, Long amount){
        return NewWalletTransactionRequest.builder().username(username).isDebit(isDebit).amount(amount).build();
    }
    public void matchWithMarketBuyOrders() {

    }

    public void matchWithMarketSellOrders() {

    }
}
