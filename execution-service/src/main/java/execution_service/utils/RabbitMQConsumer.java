package execution_service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import execution_service.entity.OrderExecutionMessage;
import execution_service.entity.StockTransaction;

@Service
public class RabbitMQConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);
    @RabbitListener(queues = {"${rabbitmq.queue_listener.name}"})
    public void consumeMessage(OrderExecutionMessage orderExecutionMessage){
        LOGGER.info(String.format("Received message -> %s \n", orderExecutionMessage.toString()));
    }
}