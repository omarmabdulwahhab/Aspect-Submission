package com.bostman.dto;

import com.bostman.entity.DeliveryStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeliveryResponseDTO {
    private String trackingId;
    private String pickupAddress;
    private String recipientAddress;
    private DeliveryStatus status;
    private String driverEmail;
    private String description;
    private String recipientName;
}
