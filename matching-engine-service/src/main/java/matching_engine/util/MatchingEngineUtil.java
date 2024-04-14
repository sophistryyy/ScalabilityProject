package matching_engine.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import matching_engine.entity.OrderBook;
import matching_engine.entity.OrderExecutionMessage;
import matching_engine.entity.StockTransaction;
import matching_engine.entity.enums.OrderStatus;
import matching_engine.entity.enums.OrderType;
import matching_engine.repositories.QueuedStockRepository;
import matching_engine.requests.InternalUpdateUserStockRequest;
import matching_engine.requests.MarketOrderHandlerResponse;
import matching_engine.requests.NewStockTransactionRequest;
import matching_engine.requests.NewWalletTransactionRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@RequiredArgsConstructor
@Slf4j
@Service
public class MatchingEngineUtil {

    private final RabbitMQProducer producer;
    private final OrderBook orderBook;
    private final MarketOrderChecker marketOrderChecker;
    private  final QueuedStockRepository stockRepository;

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
        LinkedList<StockTransaction> lstOfBuyMarketStocks = orderBook.getBuyMarketOrdersByStockId(stockId);

        if(lstOfSellStocks == null || lstOfSellStocks.isEmpty()) return;

        if(lstOfBuyMarketStocks != null && !lstOfBuyMarketStocks.isEmpty()){
            //match market orders first
            for(StockTransaction marketBuyOrder: lstOfBuyMarketStocks){

                for(StockTransaction sellOrder: lstOfSellStocks){
                    if(sellOrder.getOrderType() == OrderType.MARKET){
                        continue;
                    }
                    if(marketBuyOrder.getTrueRemainingQuantity() <= 0 || sellOrder.getTrueRemainingQuantity() <= 0){
                        continue;
                    }

                    //No market sell orders, only LIMIT ones
                    Long sellingPrice =  sellOrder.getPrice();
                    Long sellingStocks = sellOrder.getTrueRemainingQuantity();

                    MarketOrderHandlerResponse userHasEnough = marketOrderChecker.checkIfMarketOrderHasEnough
                            (marketBuyOrder.getUsername(), sellingPrice,sellingStocks);
                    if(!userHasEnough.success()){//transaction can't happen, so skip
                        break;
                    }
                    //user paid for stocks
                    Long buyingStocks = userHasEnough.buyingStocks();//this how much user can afford to buy
                    OrderStatus buyOrderStatus = marketBuyOrder.getOrderStatus();
                    OrderStatus sellOrderStatus = sellOrder.getOrderStatus();

                    NewStockTransactionRequest stockTransactionRequest;
                    NewWalletTransactionRequest walletTransactionRequest;
                    InternalUpdateUserStockRequest updateUserStockRequest;
                    OrderExecutionMessage orderMessage;

                    if(buyingStocks > sellingStocks) // user wants to buy more than top seller has
                    {
                        orderMessage = handleOrdersCompleted(sellOrder, sellingStocks, sellingPrice, false);
                        //set seller to be completed
                        //set true remaining q to 0
                        sellOrder.setOrderStatus(OrderStatus.COMPLETED);
                        sellOrder.setTrueRemainingQuantity(0L);
                        orderBook.popSellOrder(stockId);
                        //remove from db
                        producer.sendMessage(orderMessage);

                        //remember this is a market buy order
                        marketBuyOrder.setOrderStatus(OrderStatus.PARTIAL_FULFILLED);
                        marketBuyOrder.setTrueRemainingQuantity(marketBuyOrder.getTrueRemainingQuantity() - sellingStocks);//still more to go
                        orderMessage = handleOrdersPartiallyFulfilled(marketBuyOrder,sellingStocks, sellingPrice, true);
                        producer.sendMessage(orderMessage);
                    }else if(buyingStocks < sellingStocks){ //user either can't afford all of the stocks or just doesn't want to buy that many
                        //SELL is partially completed
                        sellOrder.setOrderStatus(OrderStatus.PARTIAL_FULFILLED);
                        sellOrder.setTrueRemainingQuantity(sellingStocks - buyingStocks);//still more to go
                        orderMessage = handleOrdersPartiallyFulfilled(sellOrder,buyingStocks, sellingPrice, false);
                        producer.sendMessage(orderMessage);

                        //Buy might be completed or doesn't have enough
                        if(marketBuyOrder.getTrueRemainingQuantity().equals(buyingStocks)){//completed
                            orderMessage = handleOrdersCompleted(marketBuyOrder, buyingStocks, sellingPrice, true);
                            marketBuyOrder.setOrderStatus(OrderStatus.COMPLETED);
                            marketBuyOrder.setTrueRemainingQuantity(0L);
                            orderBook.popBuyMarketOrder(stockId);
                            producer.sendMessage(orderMessage);
                            //remove from db
                        }//partially fulfilled but user doesn't have enought to pay
                        else{
                            marketBuyOrder.setOrderStatus(OrderStatus.PARTIAL_FULFILLED);
                            marketBuyOrder.setTrueRemainingQuantity(sellingStocks - marketBuyOrder.getTrueRemainingQuantity());//still more to go
                            orderMessage = handleOrdersPartiallyFulfilled(marketBuyOrder,buyingStocks, sellingPrice, true);
                            producer.sendMessage(orderMessage);
                            break;

                        }
                    }else{//sell is completed, buy can be either completed or user doesn't have neough
                        orderMessage = handleOrdersCompleted(sellOrder, sellingStocks, sellingPrice, false);
                        sellOrder.setOrderStatus(OrderStatus.COMPLETED);
                        sellOrder.setTrueRemainingQuantity(0L);
                        orderBook.popSellOrder(stockId);
                        //remove from db
                        producer.sendMessage(orderMessage);

                        if(marketBuyOrder.getTrueRemainingQuantity().equals(buyingStocks)){//completed
                            orderMessage = handleOrdersCompleted(marketBuyOrder, buyingStocks, sellingPrice, true);
                            marketBuyOrder.setOrderStatus(OrderStatus.COMPLETED);
                            marketBuyOrder.setTrueRemainingQuantity(0L);
                            orderBook.popBuyMarketOrder(stockId);
                            //remove from db
                            producer.sendMessage(orderMessage);
                        }//partially fulfilled but user doesn't have enought to pay
                        else{
                            marketBuyOrder.setOrderStatus(OrderStatus.PARTIAL_FULFILLED);
                            marketBuyOrder.setTrueRemainingQuantity(sellingStocks - marketBuyOrder.getTrueRemainingQuantity());//still more to go
                            orderMessage = handleOrdersPartiallyFulfilled(marketBuyOrder,buyingStocks, sellingPrice, true);
                            producer.sendMessage(orderMessage);
                            break;
                        }
                    }
                }
            }
        }

        //get renewed lst of sell orders because market orders could've changed it.
        lstOfSellStocks = orderBook.getSellOrdersByStockId(stockId);

        LinkedList<StockTransaction> lstOfBuyStocks = orderBook.getBuyOrdersByStockId(stockId);
        if(lstOfBuyStocks != null && !lstOfBuyStocks.isEmpty()){
            //match LIMIT buy market with either Market  or Limit sell
            StockTransaction buyOrder =  lstOfBuyStocks.getFirst(); //guaranteed to be LIMIT!
            StockTransaction sellOrder = lstOfSellStocks.getFirst();

            Long sellingPrice = sellOrder.getPrice() != null ? sellOrder.getPrice() : buyOrder.getPrice(); //handle Market Sell orders
            Long sellingStocks = sellOrder.getTrueRemainingQuantity();
            //Long buyingPrice = buyOrder.getPrice();
            Long buyingStocks = buyOrder.getTrueRemainingQuantity();

            if (!orderBook.isLimitOrdersMatchPossible(stockId)) {//match isn't possible at all
                return;
            } //buyer's price is guaranteed to be higher if limit. BUY IS POSSIBLE

            OrderExecutionMessage orderMessage;
            if(buyingStocks > sellingStocks) // user wants to buy more than top seller has
            {
            /*
                SELL is completed now, buyer is getting sellingStocks quantity with price of sellingPrice
             */
                OrderStatus prevOrderStatus = sellOrder.getOrderStatus();
                orderMessage = handleOrdersCompleted(sellOrder, sellingStocks, sellingPrice, false);
                orderBook.popSellOrder(stockId);
                //remove from db
                deleteFromDb(stockId);
                producer.sendMessage(orderMessage);

                //set seller to be completed
                //set true remaining q to 0
                sellOrder.setOrderStatus(OrderStatus.COMPLETED);
                sellOrder.setTrueRemainingQuantity(0L);
                if(prevOrderStatus == OrderStatus.PARTIAL_FULFILLED){
                    orderMessage = handleOriginalOrdersFromPartialToCompleted(sellOrder);
                    producer.sendMessage(orderMessage);
                }

                //BUY ORDER
                buyOrder.setOrderStatus(OrderStatus.PARTIAL_FULFILLED);
                buyOrder.setTrueRemainingQuantity(buyingStocks - sellingStocks);//still more to go
                orderMessage = handleOrdersPartiallyFulfilled(buyOrder,sellingStocks, sellingPrice, true);
                producer.sendMessage(orderMessage);
            }else if(buyingStocks < sellingStocks){
                //SELL is partially completed
                sellOrder.setOrderStatus(OrderStatus.PARTIAL_FULFILLED);
                sellOrder.setTrueRemainingQuantity(sellingStocks - buyingStocks);//still more to go
                orderMessage = handleOrdersPartiallyFulfilled(sellOrder,buyingStocks, sellingPrice, false);
                producer.sendMessage(orderMessage);


                //Buy is completed
                orderMessage = handleOrdersCompleted(buyOrder, buyingStocks, sellingPrice, true);
                buyOrder.setOrderStatus(OrderStatus.COMPLETED);
                buyOrder.setTrueRemainingQuantity(0L);
                orderBook.popBuyOrder(stockId);
                deleteFromDb(stockId);
                producer.sendMessage(orderMessage);
            }else{
                orderMessage = handleOrdersCompleted(sellOrder, sellingStocks, sellingPrice, false);
                sellOrder.setOrderStatus(OrderStatus.COMPLETED);
                sellOrder.setTrueRemainingQuantity(0L);
                orderBook.popSellOrder(stockId);
                deleteFromDb(stockId);
                producer.sendMessage(orderMessage);

                //Buy is completed
                orderMessage = handleOrdersCompleted(buyOrder, buyingStocks, sellingPrice, true);
                buyOrder.setOrderStatus(OrderStatus.COMPLETED);
                buyOrder.setTrueRemainingQuantity(0L);
                orderBook.popBuyOrder(stockId);
                deleteFromDb(stockId);
                producer.sendMessage(orderMessage);
            }
            match(stockId);
            System.out.println("\n" + orderBook.toString() + "\n");
        }

    }
    /*
    so i just went through logic and the requests i send you are:
    case 1 Creating child stock tx (original got partially fulfilled):
          if Buy:
            NewStockTx:
                stock_tx_id is null
                wallet_tx_id is null
            NewStockTX, NewWallet Requests and AddStockToUser are not null.
          if Sell:
            same thing, but AddStockToUser is null
          Don't subtract money from Buyer, but Add money to Seller and add portoflio stock to Buyer
    case 2 Updating original stock tx
        if Buy:
            NewStockTx:
                stock_tx_id is not null
                wallet_tx_id is either null or n
                ot null

            if wallet_tx_id is null:


            NewWallet Requests is null if limit. NewWallet Requests is not null if market order
            AddStockToUser and  NewStock are not null. x
        If Sell:
            stock_tx_id is not null
            wallet_tx_id is null
            NewStock, NewWallet Requests are not null. AddStockToUser is  null.
        Don't subtract money from Buyer, but Add money to Seller and add portoflio stock to Buyer
     */
    private InternalUpdateUserStockRequest createUpdateUserStockRequest(String username, Long stockId, Long quantity) {
        return new InternalUpdateUserStockRequest(stockId, quantity, username, true);
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

    public OrderExecutionMessage handleOrdersCompleted(StockTransaction order, Long quantity, Long price, Boolean isDebit) {
        NewStockTransactionRequest stockTransactionRequest;
        NewWalletTransactionRequest walletTransactionRequest = null;
        InternalUpdateUserStockRequest updateUserStockRequest = null;

        if(order.getOrderStatus() == OrderStatus.PARTIAL_FULFILLED) {
            //create child stock tx
            stockTransactionRequest = createNewChildStockTx(order, quantity, OrderStatus.COMPLETED,price);//stock_tx_id is null!
        }else{
            //set wallet TX to this origin seller
            stockTransactionRequest = createNewChildStockTx(order, quantity,OrderStatus.COMPLETED,price);
            stockTransactionRequest.setStock_tx_id(order.getStock_tx_id());
            stockTransactionRequest.setParent_stock_tx_id(null);
        }
        if(!order.getIs_buy()) {
            walletTransactionRequest = createNewWalletTx(order.getUsername(), isDebit, price*quantity);
        }else{
            updateUserStockRequest = createUpdateUserStockRequest(order.getUsername(), order.getStock_id(), quantity);
        }

        if(order.getOrderType() == OrderType.MARKET && order.getIs_buy()){
            walletTransactionRequest = createNewWalletTx(order.getUsername(), isDebit, price*quantity);
        }

        return new OrderExecutionMessage(stockTransactionRequest, walletTransactionRequest, updateUserStockRequest, false);
    }

    public OrderExecutionMessage handleOrdersPartiallyFulfilled(StockTransaction order, Long quantity, Long price, Boolean isDebit ) {
        NewStockTransactionRequest stockTransactionRequest;
        NewWalletTransactionRequest walletTransactionRequest;
        InternalUpdateUserStockRequest updateUserStockRequest = null;

        stockTransactionRequest = createNewChildStockTx(order, quantity,  OrderStatus.COMPLETED, price);//stock_tx_id is null!
        walletTransactionRequest = createNewWalletTx(order.getUsername(), isDebit, price*quantity);

        if(order.getIs_buy()) {
            updateUserStockRequest = createUpdateUserStockRequest(order.getUsername(), order.getStock_id(), quantity);
        }

        return new OrderExecutionMessage(stockTransactionRequest, walletTransactionRequest, updateUserStockRequest, false);
    }

    public OrderExecutionMessage handleOriginalOrdersFromPartialToCompleted(StockTransaction order){
        NewStockTransactionRequest stockTransactionRequest = NewStockTransactionRequest.builder().stock_tx_id(order.getStock_tx_id())
                .stockId(order.getStock_id()).parent_stock_tx_id(null).orderStatus(OrderStatus.COMPLETED).isBuy(order.getIs_buy())
                .price(order.getPrice()).quantity(order.getQuantity()).walletTXid(null).username(order.getUsername()).build();

        return new OrderExecutionMessage(stockTransactionRequest, null, null, false);
    }
    public void deleteFromDb(Long stockId){
        stockRepository.deleteById(stockId);
    }
}
