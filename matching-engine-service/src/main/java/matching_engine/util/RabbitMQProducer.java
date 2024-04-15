package matching_engine.util;

import lombok.RequiredArgsConstructor;
import matching_engine.entity.OrderExecutionMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQProducer {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing_key.name}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQProducer.class);

    public void sendMessage(OrderExecutionMessage orderExecutionMessage){
        rabbitTemplate.convertAndSend(exchange, routingKey, orderExecutionMessage);
        LOGGER.info(String.format("Message sent -> %s", orderExecutionMessage.toString()));
    }
}