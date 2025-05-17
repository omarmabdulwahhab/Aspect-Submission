package com.bostman.service;

import com.bostman.dto.EmailMessage;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${bostman.queue.email}")
    public void consume(EmailMessage message) {
        System.out.println("Consumed email message: "+ message.getBody());
        notificationService.sendEmail(message.getTo(), message.getSubject(), message.getBody());
    }
}
