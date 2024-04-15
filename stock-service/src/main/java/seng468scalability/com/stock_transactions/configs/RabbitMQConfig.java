package seng468scalability.com.stock_transactions.configs;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

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
    @Lazy(value = false)
    public Queue mqueue(){
        return new Queue(queue);
    }

    @Bean
    @Lazy(value = false)
    public Queue cancelOrderQueue(){
        return new Queue(cancelOrderQueue);
    }

    @Bean
    @Lazy(value = false)
    public TopicExchange topicExchange(){
        return new TopicExchange(exchange);
    }

    @Bean
    @Lazy(value = false)
    public Binding binding(){
        return BindingBuilder.bind(mqueue()).
                to(topicExchange())
                .with(routingKey);
    }

    @Bean
    @Lazy(value = false)
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
