package matching_engine.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import matching_engine.entity.OrderBook;
import matching_engine.entity.OrderExecutionMessage;
import matching_engine.entity.StockTransaction;
import matching_engine.entity.enums.OrderStatus;
import matching_engine.entity.enums.OrderType;
import matching_engine.repositories.QueuedStockRepository;
import matching_engine.requests.MarketOrderHandlerResponse;
import matching_engine.requests.NewStockTransactionRequest;
import matching_engine.requests.NewWalletTransactionRequest;
import matching_engine.requests.UpdateUserStockRequest;
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
    /*
 From IN Progress to Completed:
     if Buy :
         If Limit:
             stockTx already exist, don't update
             walletTx already exist, don't update

         If Market:
             stockTx already exist, don't update
             walletTx doesn't exist, so use NewWalletTx and set it stock TX and update

         Add stock to user
     if Sell :
         stockTx already exist, don't update
         Add money to user
         Create wallet tx according to NewWalletTx
     Make order Completed

  From IN Progress to Partial Fulfilled:
     If Buy:
         create child stock tx (no stock_tx_id, and walletTx id)
         create wallet tx from NewWalletTx
         money was already deducted
         set child to Completed
         If Limit:
             set original to partially fulfilled
             stockTx already exist, don't update
             walletTx already exist, don't update
         If Market:
             set original to partially fulfilled
             stockTx already exist, don't update
             walletTx is null, don't update
         Add stock to user
      If sell:
         create child stock tx (no stock_tx_id, and walletTx id)
         create wallet tx from NewWalletTx
         set child to Completed
         If Limit:
             set original to partially fulfilled
             stockTx already exist, don't update
             walletTx is null, don't update
         If Market:
             set original to partially fulfilled
             stockTx already exist, don't update
             walletTx is null, don't update

    From Partial to Completed:
         If buy:
             create child stock tx (no stock_tx_id, and walletTx id)
             create wallet tx from NewWalletTx
             money was already deducted
             set child to Completed
             If Limit:
                 set original to completed
                 stockTx already exist, don't update
                 walletTx already exist, don't update (Have to delete this in the future)
             If Market:
                 set original to completed
                 stockTx already exist, don't update
                 walletTx is null, don't update
         If sell:
             create child stock tx (no stock_tx_id, and walletTx id)
             create wallet tx from NewWalletTx
             set child to Completed
             If Limit:
                 set original to completed
                 stockTx already exist, don't update
                 walletTx is null, don't update
             If Market:
                 set original to completed
                 stockTx already exist, don't update
                 walletTx is null, don't update
  */

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
                            (marketBuyOrder.getUsername(), sellingPrice,sellingStocks, marketBuyOrder.getTrueRemainingQuantity());
                    if(!userHasEnough.success()){//transaction can't happen, so skip
                        break;
                    }
                    //user paid for stocks
                    Long buyingStocks = userHasEnough.buyingStocks();//this how much user can afford to buy
                    OrderStatus buyOrderStatus = marketBuyOrder.getOrderStatus();


                    OrderExecutionMessage orderMessage;

                    if(buyingStocks > sellingStocks) // user wants to buy more than top seller has
                    {
                        if(sellOrder.getOrderStatus() == OrderStatus.PARTIAL_FULFILLED){
                            orderMessage = handleOriginalOrdersFromPartialToCompleted(sellOrder);
                            producer.sendMessage(orderMessage);
                        }
                        orderMessage = handleOrdersCompleted(sellOrder, sellingStocks, sellingPrice, false);
                        //set seller to be completed
                        //set true remaining q to 0
                        sellOrder.setOrderStatus(OrderStatus.COMPLETED);
                        sellOrder.setTrueRemainingQuantity(0L);
                        orderBook.popSellOrder(stockId);
                        //remove from db
                        producer.sendMessage(orderMessage);


                        //remember this is a market buy order
                        if(marketBuyOrder.getOrderStatus() == OrderStatus.IN_PROGRESS) {
                            orderMessage = handleOriginalOrdersFromInProgressToPartial(marketBuyOrder);
                            producer.sendMessage(orderMessage);//update from inprogress to partial
                        }
                        marketBuyOrder.setOrderStatus(OrderStatus.PARTIAL_FULFILLED);
                        marketBuyOrder.setTrueRemainingQuantity(marketBuyOrder.getTrueRemainingQuantity() - sellingStocks);//still more to go
                        orderMessage = handleOrdersPartiallyFulfilled(marketBuyOrder,sellingStocks, sellingPrice, true);
                        producer.sendMessage(orderMessage);

                    }else if(buyingStocks < sellingStocks){ //user either can't afford all of the stocks or just doesn't want to buy that many
                        //SELL is partially completed
                        if(sellOrder.getOrderStatus() == OrderStatus.IN_PROGRESS) {
                            orderMessage = handleOriginalOrdersFromInProgressToPartial(sellOrder);
                            producer.sendMessage(orderMessage);//update from inprogress to partial
                        }

                        sellOrder.setOrderStatus(OrderStatus.PARTIAL_FULFILLED);
                        sellOrder.setTrueRemainingQuantity(sellingStocks - buyingStocks);//still more to go
                        orderMessage = handleOrdersPartiallyFulfilled(sellOrder,buyingStocks, sellingPrice, false);
                        producer.sendMessage(orderMessage);


                        //Buy might be completed or doesn't have enough
                        if(marketBuyOrder.getTrueRemainingQuantity().equals(buyingStocks)){//completed
                            if(marketBuyOrder.getOrderStatus() == OrderStatus.PARTIAL_FULFILLED){
                                orderMessage = handleOriginalOrdersFromPartialToCompleted(sellOrder);
                                producer.sendMessage(orderMessage);
                            }

                            orderMessage = handleOrdersCompleted(marketBuyOrder, buyingStocks, sellingPrice, true);
                            marketBuyOrder.setOrderStatus(OrderStatus.COMPLETED);
                            marketBuyOrder.setTrueRemainingQuantity(0L);
                            orderBook.popBuyMarketOrder(stockId);
                            producer.sendMessage(orderMessage);

                            //remove from db
                        }//partially fulfilled but user doesn't have enought to pay
                        else{
                            if(marketBuyOrder.getOrderStatus() == OrderStatus.IN_PROGRESS) {
                                orderMessage = handleOriginalOrdersFromInProgressToPartial(sellOrder);
                                producer.sendMessage(orderMessage);//update from inprogress to partial
                            }
                            marketBuyOrder.setOrderStatus(OrderStatus.PARTIAL_FULFILLED);
                            marketBuyOrder.setTrueRemainingQuantity(sellingStocks - marketBuyOrder.getTrueRemainingQuantity());//still more to go
                            orderMessage = handleOrdersPartiallyFulfilled(marketBuyOrder,buyingStocks, sellingPrice, true);
                            producer.sendMessage(orderMessage);

                            break;
                        }
                    }else{//sell is completed, buy can be either completed or user doesn't have neough
                        if(sellOrder.getOrderStatus() == OrderStatus.PARTIAL_FULFILLED){
                            orderMessage = handleOriginalOrdersFromPartialToCompleted(sellOrder);
                            producer.sendMessage(orderMessage);
                        }
                        orderMessage = handleOrdersCompleted(sellOrder, sellingStocks, sellingPrice, false);
                        //set seller to be completed
                        //set true remaining q to 0
                        sellOrder.setOrderStatus(OrderStatus.COMPLETED);
                        sellOrder.setTrueRemainingQuantity(0L);
                        orderBook.popSellOrder(stockId);
                        //remove from db
                        producer.sendMessage(orderMessage);

                        //Buy might be completed or doesn't have enough
                        if(marketBuyOrder.getTrueRemainingQuantity().equals(buyingStocks)){//completed
                            if(marketBuyOrder.getOrderStatus() == OrderStatus.PARTIAL_FULFILLED){
                                orderMessage = handleOriginalOrdersFromPartialToCompleted(sellOrder);
                                producer.sendMessage(orderMessage);
                            }

                            orderMessage = handleOrdersCompleted(marketBuyOrder, buyingStocks, sellingPrice, true);
                            marketBuyOrder.setOrderStatus(OrderStatus.COMPLETED);
                            marketBuyOrder.setTrueRemainingQuantity(0L);
                            orderBook.popBuyMarketOrder(stockId);
                            producer.sendMessage(orderMessage);

                            //remove from db
                        }//partially fulfilled but user doesn't have enought to pay
                        else{
                            if(marketBuyOrder.getOrderStatus() == OrderStatus.IN_PROGRESS) {
                                orderMessage = handleOriginalOrdersFromInProgressToPartial(sellOrder);
                                producer.sendMessage(orderMessage);//update from inprogress to partial
                            }
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
                //SELL is completed now, buyer is getting sellingStocks quantity with price of sellingPrice

                if(sellOrder.getOrderStatus() == OrderStatus.PARTIAL_FULFILLED){ //if from partial to complete
                    orderMessage = createChildStockTx(sellOrder,sellingStocks,sellingPrice,false);
                    producer.sendMessage(orderMessage);
                    orderMessage = handleOriginalOrdersFromPartialToCompleted(sellOrder);
                    producer.sendMessage(orderMessage);
                }else{//In progres sto Completed
                    orderMessage = handleOriginalOrdersFromInProgressToCompleted(sellOrder,sellingStocks,sellingPrice,false);
                    producer.sendMessage(orderMessage);
                }
                //setting to completed just in case
                sellOrder.setOrderStatus(OrderStatus.COMPLETED);
                sellOrder.setTrueRemainingQuantity(0L);
                deleteFromDb(sellOrder.getStock_tx_id()); //remove from db
                orderBook.popSellOrder(stockId);


                //BUY ORDER is partial fulfilled
                if(buyOrder.getOrderStatus() == OrderStatus.IN_PROGRESS) {//in progress to partial
                    orderMessage = handleOriginalOrdersFromInProgressToPartial(buyOrder);
                    producer.sendMessage(orderMessage);//update from inprogress to partial
                }//if from partial to partial no updates
                buyOrder.setOrderStatus(OrderStatus.PARTIAL_FULFILLED);
                buyOrder.setTrueRemainingQuantity(buyingStocks - sellingStocks);//still more to go
                orderMessage = createChildStockTx(buyOrder,sellingStocks, sellingPrice, true);
                producer.sendMessage(orderMessage);

            }else if(buyingStocks < sellingStocks){ //seller has more than buyer
                //SELL is partially completed
                if(sellOrder.getOrderStatus() == OrderStatus.IN_PROGRESS) {//in progress to partial
                    orderMessage = handleOriginalOrdersFromInProgressToPartial(sellOrder);
                    producer.sendMessage(orderMessage);//update from in progress to partial
                }
                sellOrder.setOrderStatus(OrderStatus.PARTIAL_FULFILLED);
                sellOrder.setTrueRemainingQuantity(sellingStocks - buyingStocks);//still more to go
                orderMessage = createChildStockTx(sellOrder, buyingStocks, sellingPrice, false);
                producer.sendMessage(orderMessage);


                //Buy is completed
                if(buyOrder.getOrderStatus() == OrderStatus.PARTIAL_FULFILLED){
                    orderMessage = createChildStockTx(buyOrder,buyingStocks,sellingPrice,true);
                    producer.sendMessage(orderMessage);
                    orderMessage = handleOriginalOrdersFromPartialToCompleted(buyOrder);
                    producer.sendMessage(orderMessage);
                }else{//from in progress to partial
                    orderMessage = handleOriginalOrdersFromInProgressToCompleted(buyOrder,buyingStocks,sellingPrice,true);
                    producer.sendMessage(orderMessage);
                }
                buyOrder.setOrderStatus(OrderStatus.COMPLETED);
                buyOrder.setTrueRemainingQuantity(0L);
                orderBook.popBuyOrder(stockId);
                deleteFromDb(buyOrder.getStock_tx_id());

            }else{
                if(sellOrder.getOrderStatus() == OrderStatus.PARTIAL_FULFILLED){ //if from partial to complete
                    orderMessage = createChildStockTx(sellOrder,sellingStocks,sellingPrice,false);
                    producer.sendMessage(orderMessage);
                    orderMessage = handleOriginalOrdersFromPartialToCompleted(sellOrder);
                    producer.sendMessage(orderMessage);
                }else{//In progres sto Completed
                    orderMessage = handleOriginalOrdersFromInProgressToCompleted(sellOrder,sellingStocks,sellingPrice,false);
                    producer.sendMessage(orderMessage);
                }
                //setting to completed just in case
                sellOrder.setOrderStatus(OrderStatus.COMPLETED);
                sellOrder.setTrueRemainingQuantity(0L);
                deleteFromDb(sellOrder.getStock_tx_id()); //remove from db
                orderBook.popSellOrder(stockId);

                //Buy is completed
                if(buyOrder.getOrderStatus() == OrderStatus.PARTIAL_FULFILLED){
                    orderMessage = createChildStockTx(buyOrder,buyingStocks,sellingPrice,true);
                    producer.sendMessage(orderMessage);
                    orderMessage = handleOriginalOrdersFromPartialToCompleted(buyOrder);
                    producer.sendMessage(orderMessage);
                }else{//from in progress to partial
                    orderMessage = handleOriginalOrdersFromInProgressToCompleted(buyOrder,buyingStocks,sellingPrice,true);
                    producer.sendMessage(orderMessage);
                }
                buyOrder.setOrderStatus(OrderStatus.COMPLETED);
                buyOrder.setTrueRemainingQuantity(0L);
                orderBook.popBuyOrder(stockId);
                deleteFromDb(buyOrder.getStock_tx_id());
            }
            match(stockId);
            System.out.println("\n" + orderBook.toString() + "\n");
        }

    }





    public OrderExecutionMessage handleOriginalOrdersFromInProgressToCompleted(StockTransaction order, Long quantity, Long price, Boolean isDebit ) {
        NewStockTransactionRequest stockTransactionRequest;
        NewWalletTransactionRequest walletTransactionRequest = null;
        UpdateUserStockRequest addStockToUserRequest = null;

        stockTransactionRequest = NewStockTransactionRequest.builder().stock_tx_id(order.getStock_tx_id())
                .stockId(order.getStock_id()).parent_stock_tx_id(null).orderStatus(OrderStatus.COMPLETED).isBuy(order.getIs_buy())
                .price(price).quantity(quantity).walletTXid(order.getWalletTXid()).username(order.getUsername()).build();


        if(order.getOrderType() == OrderType.MARKET) {
            walletTransactionRequest = createNewWalletTx(order.getUsername(), isDebit, price * quantity);
        }

        if(order.getIs_buy()) {
            addStockToUserRequest = createNewAddStockToUserRequest(order.getUsername(), order.getStock_id(), quantity);
        }

        return new OrderExecutionMessage(stockTransactionRequest, walletTransactionRequest, addStockToUserRequest, false );
    }

    public OrderExecutionMessage handleOriginalOrdersFromInProgressToPartial(StockTransaction order){
        NewStockTransactionRequest stockTransactionRequest = NewStockTransactionRequest.builder().stock_tx_id(order.getStock_tx_id())
                .stockId(order.getStock_id()).parent_stock_tx_id(null).orderStatus(OrderStatus.PARTIAL_FULFILLED).isBuy(order.getIs_buy())
                .price(order.getPrice()).quantity(order.getQuantity()).walletTXid(order.getWalletTXid()).username(order.getUsername()).build();

        return new OrderExecutionMessage(stockTransactionRequest, null, null, false);
    }

    public OrderExecutionMessage createChildStockTx(StockTransaction order, Long quantity, Long price, Boolean isDebit) {
        //use with Partially fulfilled
        NewStockTransactionRequest stockTransactionRequest;
        NewWalletTransactionRequest walletTransactionRequest;
        UpdateUserStockRequest addStockToUserRequest = null;

        //create child stock tx
        stockTransactionRequest = createNewChildStockTx(order, quantity, OrderStatus.COMPLETED,price);//stock_tx_id is null!
        walletTransactionRequest = createNewWalletTx(order.getUsername(), isDebit, price*quantity);

        if(order.getIs_buy()){
            addStockToUserRequest = createNewAddStockToUserRequest(order.getUsername(), order.getStock_id(), quantity);
        }

        return new OrderExecutionMessage(stockTransactionRequest, walletTransactionRequest, addStockToUserRequest, false);
    }

    public OrderExecutionMessage handleOriginalOrdersFromPartialToCompleted(StockTransaction order){
        NewStockTransactionRequest stockTransactionRequest = NewStockTransactionRequest.builder().stock_tx_id(order.getStock_tx_id())
                .stockId(order.getStock_id()).parent_stock_tx_id(null).orderStatus(OrderStatus.COMPLETED).isBuy(order.getIs_buy())
                .price(order.getPrice()).quantity(order.getQuantity()).walletTXid(order.getWalletTXid()).username(order.getUsername()).build();

        return new OrderExecutionMessage(stockTransactionRequest, null, null, false );
    }

    //-------------------------------------------------------------------------------------------------------------//
    private UpdateUserStockRequest createNewAddStockToUserRequest(String username, Long stockId, Long quantity) {
        return new UpdateUserStockRequest(stockId, quantity,username, true);
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

    public void deleteFromDb(Long stockTxId){
        stockRepository.deleteById(stockTxId);
    }
}
