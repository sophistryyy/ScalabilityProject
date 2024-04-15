package seng468scalability.com.stock_transactions.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import seng468scalability.com.portfolio.request.AddStockToUserRequest;
import seng468scalability.com.stock_transactions.entity.StockTransaction;
import seng468scalability.com.stock_transactions.entity.enums.OrderStatus;
import seng468scalability.com.stock_transactions.entity.enums.OrderType;
import seng468scalability.com.stock_transactions.repositories.StockTransactionsRepository;
import seng468scalability.com.stock_transactions.request.CancelOrderRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CancelOrderService {
    private final StockTransactionsRepository stockTransactionsRepository;
    private final MongoTemplate mongoTemplate;
    private final RabbitMQProducer producer;
    private final StockOrderUtil stockOrderUtil;
    public String try_cancelling(CancelOrderRequest req, String username) {
        Long stock_tx_id = req.getStock_tx_id();

        StockTransaction foundOrder = findOrder(username,stock_tx_id);
        if (foundOrder == null) {
            return "Transaction not found";
        }
        if (foundOrder.getOrderType() == OrderType.MARKET) {
            return "You can only cancel LIMIT orders";
        }
        if (foundOrder.getOrderStatus() == OrderStatus.IN_PROGRESS || foundOrder.getOrderStatus() == OrderStatus.PARTIAL_FULFILLED) {
            //send message to matching engine
            req.setOrderType(foundOrder.getOrderType());
            req.setIsBuy(foundOrder.is_buy());
            req.setStock_id(foundOrder.getStockId());
            producer.cancelOrderMessage(req);
            if(foundOrder.is_buy() && foundOrder.getOrderType() == OrderType.LIMIT){
                //return money to limit orders
                List<StockTransaction> childOrders = null;
                if(foundOrder.getOrderStatus() == OrderStatus.PARTIAL_FULFILLED){
                     childOrders = getChildOrders(foundOrder.getStock_tx_id());
                }
                stockOrderUtil.returnMoney(foundOrder, childOrders);
            }else{
                //return stock portfolio
                Long toReturnQuantity = foundOrder.getQuantity();

                List<StockTransaction> childOrders = null;
                if(foundOrder.getOrderStatus() == OrderStatus.PARTIAL_FULFILLED){
                    childOrders = getChildOrders(foundOrder.getStock_tx_id());
                    for(StockTransaction childOrder: childOrders){
                        toReturnQuantity -= childOrder.getQuantity();
                    }
                }

                AddStockToUserRequest addStockToUserRequest = new AddStockToUserRequest(foundOrder.getStockId(), toReturnQuantity);
                stockOrderUtil.addStockToUser(addStockToUserRequest, username);
            }
            stockTransactionsRepository.deleteById(stock_tx_id);
        } else {
            return "Can't cancel this order. It's either completed or expired";
        }
        return null;
    }

    public StockTransaction findOrder(String username, Long stockTxId){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id")
                .is(stockTxId)).addCriteria(Criteria.where("username").is(username));

        return mongoTemplate.findOne(query,StockTransaction.class);
    }

    public List<StockTransaction> getChildOrders(Long stock_tx_id){
        Query query = new Query();
        query.addCriteria(Criteria.where("parent_stock_tx_id").is(stock_tx_id));
        return  mongoTemplate.find(query,StockTransaction.class);
    }

}
