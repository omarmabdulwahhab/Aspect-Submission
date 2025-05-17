package com.bostman.dto;

import com.bostman.entity.DeliveryStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeliveryResponseDTO {
    private String trackingId;
    private String pickupLocation;
    private String dropoffLocation;
    private DeliveryStatus status;
    private String assignedDriver;
}
