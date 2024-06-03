
package com.test.springrabbitmq;

import com.test.SpringRabbitMQApplication2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

// this application is dedicated for sending delay message by using plugin
// in order for plugin to works for delay message we need to install the //https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases
// and put into the rabbitMQ container (if we are using docker desktop)
@SpringBootTest(classes = SpringRabbitMQApplication2.class)
@RunWith(SpringRunner.class)
public class TestDelayMessagePlugin {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testMessageDelay(){

        // once we use the delay plugin, returnMessage will get callback no matter the message send to or not send to the queue
        MessagePostProcessor messagePostProcessor = message -> {

            message.getMessageProperties().setHeader("x-delay", "10000");
            return message;

        };

        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(
                "exchange.test.delay",
                "queue.test.delay",
                "Test delay message by plugin " + new SimpleDateFormat("HH:mm:ss").format(new Date()),
                messagePostProcessor,
                correlationData
        );


    }

}
