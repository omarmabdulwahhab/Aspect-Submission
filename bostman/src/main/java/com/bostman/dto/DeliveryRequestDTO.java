package com.bostman.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeliveryRequestDTO {
    private String pickupAddress;
    private String recipientAddress;
    private String description;
    private String recipientName;
    private String recipientMobileNumber;
    private LocalDateTime scheduledTime;
}
