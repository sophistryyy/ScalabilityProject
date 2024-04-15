package seng468scalability.com.stock_transactions.configs;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.name}")
    private String queue;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing_key.name}")
    private String routingKey;

    @Value("${rabbitmq.cancelorder.queue.name}")
    private String cancelOrderQueue;

    @Value("${rabbitmq.cancelorder.routing_key.name}")
    private String cancelOrderRoutingKey;
    @Bean
    public Queue mqueue(){
        return new Queue(queue);
    }

    @Bean
    public Queue cancelOrderQueue(){
        return new Queue(cancelOrderQueue);
    }

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding binding(){
        return BindingBuilder.bind(mqueue()).
                to(topicExchange())
                .with(routingKey);
    }

    @Bean
    public Binding cancelOrderBinding(){
        return BindingBuilder.bind(cancelOrderQueue()).
                to(topicExchange())
                .with(cancelOrderRoutingKey);
    }

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
