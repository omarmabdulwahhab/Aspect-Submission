package com.bostman.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryUpdateDTO {
    private String description;
    private String recipientName;
    private String recipientMobileNumber;
    private String pickupLocation; // Ora pickupAddress, ensure consistency with Delivery entity
    private String dropoffLocation; // Or recipientAddress, ensure consistency with Delivery entity
    // Add any other fields you want to be editable, e.g.:
    // private java.time.LocalDateTime scheduledTime;
} 