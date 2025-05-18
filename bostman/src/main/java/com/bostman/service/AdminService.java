package com.bostman.service;

import com.bostman.entity.Delivery;
import com.bostman.entity.DeliveryStatus;

public interface AdminService {
    String assignDriver(String trackingId, String driverEmail);
    String updateStatus(String trackingId, DeliveryStatus status);
    Delivery assignDriverToDelivery(Long deliveryId, Long driverId);
}
