package com.test;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

// Publisher confirm is to make sure the publisher send message to the exchange
// Publisher confirm mode
// 1. Single confirm
// 2. Multiple confirm
// 3. asynchronous multiple confirm

// Publisher Return
// is to ensure message is it send to queue
// 1. Message got send to MQ, but route failed due to wrong routing key or no binding queue, publisher return callback send the cause and return ACK
@Slf4j
@Configuration
public class RabbitTemplateConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void initRabbitTemplate(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {

        // some queue does not bind to any exchange and bind with default exchange, so when send message to that queue does not put correlation data
        if(correlationData != null){
            System.out.println("接收到了RabbitMQ的回调id : " + correlationData.getId());
        }

        if (ack) {
            System.out.println("消息成功发送到exchange");
        } else {
            System.out.println("消息失败发送到exchange : " + cause);
        }

    }

    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        log.error("Received message return callback, exchange:{}, key:{}, msg:{}, code:{},text:{} ", returnedMessage.getExchange(),
                returnedMessage.getRoutingKey(), returnedMessage.getMessage(),
                returnedMessage.getReplyCode(), returnedMessage.getReplyText());
    }
}
