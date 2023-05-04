package org.gnori.client.telegram.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

  @Value("${spring.rabbitmq.queue-name}")
  private String queueName;
  @Value("${spring.rabbitmq.exchange-name}")
  private String exchangeName;

  @Bean
  public Queue queue() {
    return new Queue(queueName);
  }

  @Bean
  public FanoutExchange fanoutExchange() {
    return new FanoutExchange(exchangeName);
  }

  @Bean
  Binding binding() {
    return BindingBuilder.bind(queue()).to(fanoutExchange());
  }

}
