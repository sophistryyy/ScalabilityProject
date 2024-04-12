package matching_engine.util;

import matching_engine.entity.OrderExecutionMessage;
import matching_engine.entity.StockTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);
    @Autowired
    RabbitMQProducer rabbitMQProducer;
    @RabbitListener(queues = {"${rabbitmq.queue_listener.name}"})
    public void consumeMessage(StockTransaction message){

        LOGGER.info(String.format("Received message -> %s", message.toString()));
    }
}
