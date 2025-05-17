package com.bostman.service;

import com.bostman.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${bostman.exchange.email}")
    private String emailExchange;

    @Value("${bostman.routing-key.email}")
    private String emailRoutingKey;

    public void sendEmailMessage(EmailMessage message) {
        try {
            log.info("Attempting to send email message to exchange: {}, routing key: {}", emailExchange, emailRoutingKey);
            rabbitTemplate.convertAndSend(emailExchange, emailRoutingKey, message);
            log.info("Successfully sent email message");
        } catch (AmqpException e) {
            log.error("Failed to send message to RabbitMQ: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send message to queue", e);
        }
    }
}
