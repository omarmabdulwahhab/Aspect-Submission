package com.bostman.service;

public interface NotificationService {
    void sendEmail(String to, String subject, String body);
}
