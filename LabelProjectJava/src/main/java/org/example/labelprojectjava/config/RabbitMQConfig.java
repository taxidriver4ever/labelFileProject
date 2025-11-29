package org.example.labelprojectjava.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RabbitMQConfig {

    // 1. 定义 Fanout 交换机
    public static final String FANOUT_EXCHANGE_NAME = "fanout.uploadFile.exchange";

    @Bean
    public FanoutExchange uploadFileFanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE_NAME);
    }

    // 2. 为每个消费者定义匿名队列
    // 这些队列没有名字，由RabbitMQ自动生成，独占、自动删除
    @Bean
    public Queue uploadFileQueue1() {
        return new AnonymousQueue(); // 匿名队列，自动生成唯一名称
    }

    @Bean
    public Queue uploadFileQueue2() {
        return new AnonymousQueue();
    }

    // 3. 将队列绑定到 Fanout 交换机
    // Fanout交换机会忽略routingKey，所以这里用空字符串即可
    @Bean
    public Binding uploadFileBinding1(FanoutExchange uploadFileFanoutExchange, Queue uploadFileQueue1) {
        return BindingBuilder.bind(uploadFileQueue1).to(uploadFileFanoutExchange);
    }

    @Bean
    public Binding uploadFileBinding2(FanoutExchange uploadFileFanoutExchange, Queue uploadFileQueue2) {
        return BindingBuilder.bind(uploadFileQueue2).to(uploadFileFanoutExchange);
    }

    @Bean
    public MessageConverter messageConverter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        // 设置允许的类模式
        List<String> list = new ArrayList<>();
        list.add("java.util.*");
        list.add("java.lang.*");
        converter.setAllowedListPatterns(list);
        return converter;
    }
}
