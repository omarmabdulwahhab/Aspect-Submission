package com.bostman.service;

import com.bostman.dto.DeliveryRequestDTO;
import com.bostman.dto.DeliveryResponseDTO;
import com.bostman.entity.DeliveryStatus;
import com.bostman.entity.User;

import java.util.List;

public interface DeliveryService {
    String createDelivery(DeliveryRequestDTO request, User user);
    List<DeliveryResponseDTO> getMyDeliveries(User user);
    DeliveryResponseDTO getByTrackingId(String trackingId);
    DeliveryResponseDTO updateDeliveryStatus(String trackingId, DeliveryStatus newStatus);
}
