package com.bostman.service;

import com.bostman.entity.Delivery;
import com.bostman.entity.DeliveryStatus;
import com.bostman.entity.User;
import com.bostman.repository.DeliveryRepository;
import com.bostman.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;

    @Override
    public String assignDriver(String trackingId, String driverEmail) {
        Delivery delivery = deliveryRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        User driver = userRepository.findByEmail(driverEmail)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        delivery.setAssignedDriver(driver);
        deliveryRepository.save(delivery);

        return "Driver assigned successfully.";
    }

    @Override
    public String updateStatus(String trackingId, DeliveryStatus status) {
        Delivery delivery = deliveryRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        delivery.setStatus(status);
        deliveryRepository.save(delivery);

        return "Delivery status updated to " + status;
    }

    @Override
    public Delivery assignDriverToDelivery(Long deliveryId, Long driverId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + deliveryId));

        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        // Optional: Check if the user is actually a driver
        // if (!driver.getRoles().contains(Role.DRIVER)) { // Assuming Role enum and getRoles() method
        //     throw new RuntimeException("User with id: " + driverId + " is not a DRIVER.");
        // }

        delivery.setAssignedDriver(driver);
        return deliveryRepository.save(delivery);
    }
}
