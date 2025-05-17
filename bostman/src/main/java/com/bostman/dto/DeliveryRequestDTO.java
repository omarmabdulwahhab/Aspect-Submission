package com.bostman.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeliveryRequestDTO {
    private String pickupLocation;
    private String dropoffLocation;
    private LocalDateTime scheduledTime;
}
