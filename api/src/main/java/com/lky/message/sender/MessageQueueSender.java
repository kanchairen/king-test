package com.lky.message.sender;

import com.lky.message.RegisterMessage;
import com.lky.message.constant.ExchangeName;
import com.lky.message.constant.QueueName;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * 消息队列全局发送
 *
 * @author luckyhua
 * @version 1.0
 * @since 2018/4/2
 */
@Component
public class MessageQueueSender {

    @Inject
    RabbitTemplate rabbitTemplate;

    public void sendRegisterMessage(RegisterMessage registerMessage) {
        this.rabbitTemplate.convertAndSend(ExchangeName.GNC_EXCHANGE, QueueName.USER_REGISTER, registerMessage);
    }
}
