package com.bostman.service;

import com.bostman.dto.DeliveryRequestDTO;
import com.bostman.dto.DeliveryResponseDTO;
import com.bostman.dto.EmailMessage;
import com.bostman.entity.Delivery;
import com.bostman.entity.DeliveryStatus;
import com.bostman.entity.User;
import com.bostman.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final NotificationService notificationService;
    private final MessageProducer messageProducer;

    @Override
    public String createDelivery(DeliveryRequestDTO request, String user) {
        Delivery delivery = Delivery.builder()
                .pickupLocation(request.getPickupLocation())
                .dropoffLocation(request.getDropoffLocation())
                .scheduledTime(request.getScheduledTime())
                .status(DeliveryStatus.SCHEDULED)
                .trackingId(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .customer(user)
                .build();

        deliveryRepository.save(delivery);

        messageProducer.sendEmailMessage(new EmailMessage(
                user.getEmail(),
                "Your Delivery is Scheduled",
                "Tracking ID: " + delivery.getTrackingId() + "<br>" +
                        "Pickup: " + request.getPickupLocation() + "<br>" +
                        "Dropoff: " + request.getDropoffLocation()
        ));

        return delivery.getTrackingId();
    }

    @Override
    public List<DeliveryResponseDTO> getMyDeliveries(String user) {
        return deliveryRepository.findByCustomer(user).stream().map(d ->
                DeliveryResponseDTO.builder()
                        .trackingId(d.getTrackingId())
                        .pickupLocation(d.getPickupLocation())
                        .dropoffLocation(d.getDropoffLocation())
                        .status(d.getStatus())
                        .assignedDriver(String.valueOf(d.getAssignedDriver()))
                        .build()
        ).toList();
    }

    @Override
    public DeliveryResponseDTO getByTrackingId(String trackingId) {
        Delivery d = deliveryRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        return DeliveryResponseDTO.builder()
                .trackingId(d.getTrackingId())
                .pickupLocation(d.getPickupLocation())
                .dropoffLocation(d.getDropoffLocation())
                .status(d.getStatus())
                .assignedDriver(d.getAssignedDriver())
                .build();
    }

    @Override
    public DeliveryResponseDTO updateDeliveryStatus(String trackingId, DeliveryStatus newStatus) {
        Delivery delivery = deliveryRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Delivery not found with tracking ID: " + trackingId));

        delivery.setStatus(newStatus);
        Delivery updatedDelivery = deliveryRepository.save(delivery);

        User customer = updatedDelivery.getCustomer();
        if (customer != null) {
            String subject = "Delivery Status Updated: " + newStatus.toString();
            String body = "Your delivery with Tracking ID: " + updatedDelivery.getTrackingId() +
                    " has been updated to: " + newStatus.toString() + ".<br>" +
                    "Pickup: " + updatedDelivery.getPickupLocation() + "<br>" +
                    "Dropoff: " + updatedDelivery.getDropoffLocation();

            messageProducer.sendEmailMessage(new EmailMessage(
                    customer.getEmail(),
                    subject,
                    body
            ));
        }

        return DeliveryResponseDTO.builder()
                .trackingId(updatedDelivery.getTrackingId())
                .pickupLocation(updatedDelivery.getPickupLocation())
                .dropoffLocation(updatedDelivery.getDropoffLocation())
                .status(updatedDelivery.getStatus())
                .assignedDriver(updatedDelivery.getAssignedDriver())
                .build();
    }
}
