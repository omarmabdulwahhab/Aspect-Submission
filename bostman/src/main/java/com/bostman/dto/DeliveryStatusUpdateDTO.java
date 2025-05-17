package com.bostman.dto;

import com.bostman.entity.DeliveryStatus;
import lombok.Data;

@Data
public class DeliveryStatusUpdateDTO {
    private DeliveryStatus newStatus;
} 