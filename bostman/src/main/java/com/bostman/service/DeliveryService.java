package com.bostman.service;

import com.bostman.dto.DeliveryRequestDTO;
import com.bostman.dto.DeliveryResponseDTO;
import com.bostman.dto.DeliveryUpdateDTO;
import com.bostman.entity.DeliveryStatus;

import java.util.List;

public interface DeliveryService {
    String createDelivery(DeliveryRequestDTO request, String email);
    List<DeliveryResponseDTO> getMyDeliveries(String email);
    DeliveryResponseDTO getByTrackingId(String trackingId);
    DeliveryResponseDTO updateDeliveryStatus(String trackingId, DeliveryStatus newStatus);
    DeliveryResponseDTO updateDeliveryDetails(String trackingId, DeliveryUpdateDTO updateDTO);

    List<DeliveryResponseDTO> getAllDeliveries();
    List<DeliveryResponseDTO> getDeliveriesByDriver(String email);
    String markAsDelivered(String trackingId, String driverEmail);
    void deleteDelivery(String trackingId, String userEmail);
}
