package execution_service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import execution_service.entity.OrderExecutionMessage;
import execution_service.entity.StockTransaction;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RabbitMQConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);
    private final OrderExecutionService orderExecutionService;
    @RabbitListener(queues = {"${rabbitmq.queue_listener.name}"})
    public void consumeMessage(OrderExecutionMessage orderExecutionMessage){
        try {
            System.out.println(orderExecutionMessage.getNewStockTransaction().getOrderStatus());
            LOGGER.info(String.format("Received message -> %s", orderExecutionMessage.toString()));
            orderExecutionService.execute(orderExecutionMessage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}