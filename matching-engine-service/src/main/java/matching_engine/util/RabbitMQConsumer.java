package matching_engine.util;

import lombok.RequiredArgsConstructor;
import matching_engine.entity.OrderExecutionMessage;
import matching_engine.entity.StockTransaction;
import matching_engine.entity.enums.OrderStatus;
import matching_engine.entity.enums.OrderType;
import matching_engine.requests.CancelOrderRequest;
import matching_engine.requests.NewStockTransactionRequest;
import matching_engine.requests.NewWalletTransactionRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final MatchingEngineUtil matchingEngineUtil;
    private final CancelOrderService cancelOrderService;

    @RabbitListener(queues = {"${rabbitmq.queue_listener.name}"})
    public void consumeMessage(StockTransaction order){
        LOGGER.info(String.format("Received message -> %s", order.toString()));
        matchingEngineUtil.receiveNewElement(order);
    }

    @RabbitListener(queues = {"${rabbitmq.cancelorder_queue_listener.name}"})
    public void consumeCancelOrderMessage(CancelOrderRequest request){
        LOGGER.info(String.format("Received message to cancel order -> %s", request.toString()));
        cancelOrderService.try_cancelling(request);
    }

}
