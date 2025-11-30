package org.example.labelprojectjava.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RabbitMQConfig {

    // 1. 定义 Fanout 交换机
    public static final String FANOUT_EXCHANGE_NAME = "fanout.uploadFile.exchange";

    // 死信交换机
    public static final String DLX_EXCHANGE_NAME = "dlx.uploadFile.exchange";

    // 死信队列
    public static final String DLX_QUEUE_NAME = "dlx.uploadFile.queue";

    @Bean
    public FanoutExchange uploadFileFanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE_NAME);
    }

    // 死信交换机
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE_NAME);
    }

    // 死信队列
    @Bean
    public Queue dlxQueue() {
        return QueueBuilder.durable(DLX_QUEUE_NAME)
                .build();
    }

    // 绑定死信队列到死信交换机
    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue())
                .to(dlxExchange())
                .with("upload.file.dlx");
    }

    // 2. 为每个消费者定义队列，并配置死信交换机
    @Bean
    public Queue uploadFileQueue1() {
        return QueueBuilder.durable("upload.file.queue1") // 使用持久化队列
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE_NAME) // 设置死信交换机
                .withArgument("x-dead-letter-routing-key", "upload.file.dlx") // 设置死信路由键
                .withArgument("x-message-ttl", 10000) // 消息TTL（10秒）
                .withArgument("x-max-length", 1000) // 队列最大长度
                .build();
    }

    @Bean
    public Queue uploadFileQueue2() {
        return QueueBuilder.durable("upload.file.queue2")
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", "upload.file.dlx")
                .withArgument("x-message-ttl", 10000)
                .withArgument("x-max-length", 1000)
                .build();
    }

    // 3. 将队列绑定到 Fanout 交换机
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
        List<String> list = new ArrayList<>();
        list.add("java.util.*");
        list.add("java.lang.*");
        converter.setAllowedListPatterns(list);
        return converter;
    }
}
