package com.lky.message.config;

import com.lky.message.constant.ExchangeName;
import com.lky.message.constant.QueueName;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

/**
 * rabbit mq配置
 * 配置消息和交互机绑定，消息持久化durable=true
 *
 * @author luckyhua
 * @version 1.0
 * @since 2018/4/2
 */
@Configuration
public class RabbitConfig implements RabbitListenerConfigurer {

    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(ExchangeName.GNC_EXCHANGE);
    }


    @Bean
    public Queue userRegisterQueue() {
        return new Queue(QueueName.USER_REGISTER, Boolean.TRUE);
    }

    @Bean
    Binding bindingUserRegister(Queue userRegisterQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(userRegisterQueue).to(directExchange).with(QueueName.USER_REGISTER);
    }

    @Bean
    public Queue merchantSettledQueue() {
        return new Queue(QueueName.MERCHANT_SETTLED, Boolean.TRUE);
    }

    @Bean
    Binding bindingMerchantSettled(Queue merchantSettledQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(merchantSettledQueue).to(directExchange).with(QueueName.MERCHANT_SETTLED);
    }


    @Bean
    public MappingJackson2MessageConverter jackson2Converter() {
        return new MappingJackson2MessageConverter();
    }

    @Bean
    public DefaultMessageHandlerMethodFactory myHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(jackson2Converter());
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setDefaultRequeueRejected(Boolean.FALSE);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(myHandlerMethodFactory());
    }

}
