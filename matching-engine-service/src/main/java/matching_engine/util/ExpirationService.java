package matching_engine.util;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.AllArgsConstructor;
import matching_engine.entity.OrderExecutionMessage;
import matching_engine.entity.StockTransaction;
import matching_engine.entity.enums.OrderStatus;
import matching_engine.repositories.QueuedStockTransactionsRepository;
import matching_engine.requests.InternalUpdateUserStockRequest;
import matching_engine.requests.NewStockTransactionRequest;
import matching_engine.requests.NewWalletTransactionRequest;

@AllArgsConstructor
public class ExpirationService {
    // @Autowired
    // QueuedStockTransactionsRepository queuedStockTransactionsRepository;

    // RabbitMQProducer producer;
    
    // private final int MINUTES_TO_EXPIRE = 15;
    // @Async
    // @Scheduled(fixedRate = 500)
    // public void expireStocks() {
    //     List<StockTransaction> stockOrders = queuedStockTransactionsRepository.findAll();
    //     for (StockTransaction order : stockOrders) {   
    //         LocalDateTime timeNow = LocalDateTime.now();
    //         LocalDateTime timestampExpireTime = order.getTimestamp().plusMinutes(MINUTES_TO_EXPIRE);

    //         if (timeNow.isAfter(timestampExpireTime)) {
    //             order.setExpired(true);
    //             if (order.getIs_buy()) {
    //                 Long moneyToRefund = order.getTrueRemainingQuantity() * order.getPrice();
    //                 NewWalletTransactionRequest newWalletTransactionRequest = createNewWalletTx(order.getUsername(), true, moneyToRefund);
    //                 NewStockTransactionRequest newStockTransactionRequest = createNewStockTx(order);
    //                 OrderExecutionMessage orderExecutionMessage = new OrderExecutionMessage(newStockTransactionRequest, newWalletTransactionRequest, null, true);
    //                 producer.sendMessage(orderExecutionMessage);
    //             } else {
    //                 NewStockTransactionRequest newStockTransactionRequest = createNewStockTx(order);
    //                 InternalUpdateUserStockRequest updateUserStockRequest = createUpdateUserStockRequest(order.getUsername(), order.getStock_id(), order.getQuantity(), true);
    //                 OrderExecutionMessage orderExecutionMessage = new OrderExecutionMessage(newStockTransactionRequest, null, updateUserStockRequest, true);
    //                 producer.sendMessage(orderExecutionMessage);
    //             }
    //             queuedStockTransactionsRepository.delete(order);
    //         }

    //     }
    // }

    // public NewStockTransactionRequest createNewStockTx(StockTransaction order){
    //     return NewStockTransactionRequest.builder().stockId(order.getStock_id()).
    //             isBuy(order.getIs_buy())
    //             .orderType(order.getOrderType())
    //             .quantity(order.getQuantity())
    //             .price(order.getPrice())
    //             .orderStatus(order.getOrderStatus())
    //             .username(order.getUsername())
    //             .parent_stock_tx_id(order.getParent_stock_tx_id())
    //             .walletTXid(order.getWalletTXid())
    //             .stock_tx_id(order.getStock_tx_id()).build();
    // }

    // private InternalUpdateUserStockRequest createUpdateUserStockRequest(String username, Long stockId, Long quantity, boolean add) {
    //     return new InternalUpdateUserStockRequest(stockId, quantity, username, add);
    // }

    // public NewWalletTransactionRequest createNewWalletTx(String username, boolean isDebit, Long amount){
    //     return NewWalletTransactionRequest.builder().username(username).isDebit(isDebit).amount(amount).build();
    // }

    // private void refundMoney(StockTransaction order) {
    //     Long moneyToRefund = order.getTrueRemainingQuantity() * order.getPrice();
    //     //order.
    // }
}
