package org.example.zernovoz1.conf;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "vehicle_exchange";

    @Bean
    public TopicExchange vehicleExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue wagonQueue() {
        return new Queue("wagon_queue", true);
    }

    @Bean
    public Queue locomotiveQueue() {
        return new Queue("locomotive_queue", true);
    }

    @Bean
    public Binding wagonBinding(Queue wagonQueue, TopicExchange vehicleExchange) {
        return BindingBuilder.bind(wagonQueue).to(vehicleExchange).with("vehicle.wagon");
    }

    @Bean
    public Binding locoBinding(Queue locomotiveQueue, TopicExchange vehicleExchange) {
        return BindingBuilder.bind(locomotiveQueue).to(vehicleExchange).with("vehicle.locomotive");
    }
}
