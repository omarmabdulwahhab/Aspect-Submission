package com.bostman.service;

import com.bostman.dto.DeliveryRequestDTO;
import com.bostman.dto.DeliveryResponseDTO;
import com.bostman.entity.DeliveryStatus;

import java.util.List;

public interface DeliveryService {
    String createDelivery(DeliveryRequestDTO request, String user);
    List<DeliveryResponseDTO> getMyDeliveries(String user);
    DeliveryResponseDTO getByTrackingId(String trackingId);
    DeliveryResponseDTO updateDeliveryStatus(String trackingId, DeliveryStatus newStatus);
}
