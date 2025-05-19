package com.bostman.service;

import com.bostman.dto.DeliveryRequestDTO;
import com.bostman.dto.DeliveryResponseDTO;
import com.bostman.dto.EmailMessage;
import com.bostman.dto.DeliveryUpdateDTO;
import com.bostman.entity.Delivery;
import com.bostman.entity.DeliveryStatus;
import com.bostman.entity.User;
import com.bostman.repository.DeliveryRepository;
import com.bostman.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bostman.Annotations.DistributedLock;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final MessageProducer messageProducer;

    @Override
    public String createDelivery(DeliveryRequestDTO request, String email) {
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));

        Delivery delivery = Delivery.builder()
                .pickupLocation(request.getPickupAddress())
                .dropoffLocation(request.getRecipientAddress())
                .scheduledTime(request.getScheduledTime())
                .status(DeliveryStatus.SCHEDULED)
                .trackingId(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .customer(customer)
                .description(request.getDescription())
                .recipientName(request.getRecipientName())
                .recipientMobileNumber(request.getRecipientMobileNumber())
                .build();

        Delivery savedDelivery = deliveryRepository.save(delivery);

        String emailBody = String.format(
            "Hello %s,<br><br>"  +
            "Your delivery has been successfully scheduled! Here are the details:<br><br>" +
            "<b>Tracking ID:</b> %s<br>" +
            "<b>Status:</b> %s<br>" +
            "<b>Description:</b> %s<br>" +
            "<b>Recipient Name:</b> %s<br>" +
            "<b>Recipient Mobile:</b> %s<br>" +
            "<b>Pickup Address:</b> %s<br>" +
            "<b>Recipient Address (Dropoff):</b> %s<br><br>" +
            "Thank you for using Bostman!",
            customer.getFullName(),
            savedDelivery.getTrackingId(),
            savedDelivery.getStatus().toString(),
            savedDelivery.getDescription() != null ? savedDelivery.getDescription() : "N/A",
            savedDelivery.getRecipientName() != null ? savedDelivery.getRecipientName() : "N/A",
            savedDelivery.getRecipientMobileNumber() != null ? savedDelivery.getRecipientMobileNumber() : "N/A",
            savedDelivery.getPickupLocation() != null ? savedDelivery.getPickupLocation() : "N/A",
            savedDelivery.getDropoffLocation() != null ? savedDelivery.getDropoffLocation() : "N/A"
        );

        messageProducer.sendEmailMessage(new EmailMessage(
                customer.getEmail(),
                "Your Bostman Delivery is Scheduled - Tracking ID: " + savedDelivery.getTrackingId(),
                emailBody
        ));

        return savedDelivery.getTrackingId();
    }

    @Override
    public List<DeliveryResponseDTO> getMyDeliveries(String email) {
        return deliveryRepository.findByCustomerEmail(email).stream().map(d -> {
            String driverEmail = d.getAssignedDriver() != null ? d.getAssignedDriver().getEmail() : null;
            return DeliveryResponseDTO.builder()
                    .trackingId(d.getTrackingId())
                    .pickupAddress(d.getPickupLocation())
                    .recipientAddress(d.getDropoffLocation())
                    .status(d.getStatus())
                    .driverEmail(driverEmail)
                    .description(d.getDescription())
                    .recipientName(d.getRecipientName())
                    .recipientMobileNumber(d.getRecipientMobileNumber())
                    .build();
        }).toList();
    }

    @Override
    public DeliveryResponseDTO getByTrackingId(String trackingId) {
        Delivery d = deliveryRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Delivery not found with tracking ID: " + trackingId));

        String driverEmail = null;
        if (d.getAssignedDriver() != null) {
            driverEmail = d.getAssignedDriver().getEmail();
        }

        return DeliveryResponseDTO.builder()
                .trackingId(d.getTrackingId())
                .pickupAddress(d.getPickupLocation())
                .recipientAddress(d.getDropoffLocation())
                .status(d.getStatus())
                .driverEmail(driverEmail)
                .description(d.getDescription())
                .recipientName(d.getRecipientName())
                .recipientMobileNumber(d.getRecipientMobileNumber())
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
        
        String driverEmail = updatedDelivery.getAssignedDriver() != null ? updatedDelivery.getAssignedDriver().getEmail() : null;
        return DeliveryResponseDTO.builder()
                .trackingId(updatedDelivery.getTrackingId())
                .pickupAddress(updatedDelivery.getPickupLocation())
                .recipientAddress(updatedDelivery.getDropoffLocation())
                .status(updatedDelivery.getStatus())
                .driverEmail(driverEmail)
                .description(updatedDelivery.getDescription())
                .recipientName(updatedDelivery.getRecipientName())
                .recipientMobileNumber(updatedDelivery.getRecipientMobileNumber())
                .build();
    }

    @Override
    public List<DeliveryResponseDTO> getAllDeliveries() {
        return deliveryRepository.findAll().stream().map(d -> {
            String driverEmail = d.getAssignedDriver() != null ? d.getAssignedDriver().getEmail() : null;
            return DeliveryResponseDTO.builder()
                    .trackingId(d.getTrackingId())
                    .pickupAddress(d.getPickupLocation())
                    .recipientAddress(d.getDropoffLocation())
                    .status(d.getStatus())
                    .driverEmail(driverEmail)
                    .description(d.getDescription())
                    .recipientName(d.getRecipientName())
                    .recipientMobileNumber(d.getRecipientMobileNumber())
                    .build();
        }).toList();
    }

    @Override
    public List<DeliveryResponseDTO> getDeliveriesByDriver(String email) {
        User driver = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        return deliveryRepository.findByAssignedDriver(driver).stream()
                .map(d -> {
                    return DeliveryResponseDTO.builder()
                            .trackingId(d.getTrackingId())
                            .pickupAddress(d.getPickupLocation())
                            .recipientAddress(d.getDropoffLocation())
                            .status(d.getStatus())
                            .driverEmail(driver.getEmail())
                            .description(d.getDescription())
                            .recipientName(d.getRecipientName())
                            .recipientMobileNumber(d.getRecipientMobileNumber())
                            .build();
                }).toList();
    }

    @Override
    public String markAsDelivered(String trackingId, String driverEmail) {
        User driver = userRepository.findByEmail(driverEmail)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Delivery delivery = deliveryRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        if (!driver.equals(delivery.getAssignedDriver())) {
            throw new RuntimeException("You are not assigned to this delivery");
        }

        delivery.setStatus(DeliveryStatus.DELIVERED);
        deliveryRepository.save(delivery);

        return "Delivery marked as delivered.";
    }

    @Override
    public void deleteDelivery(String trackingId, String userEmail) {
        Delivery delivery = deliveryRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Delivery not found with tracking ID: " + trackingId));

        if (delivery.getCustomer() == null || !delivery.getCustomer().getEmail().equals(userEmail)) {
            // In a real application, you might also allow ADMIN role to delete any delivery
            // For example: check if user has ADMIN role, if so, skip customer check.
            throw new SecurityException("You are not authorized to delete this delivery.");
        }

        // Add any other business logic checks here if needed, e.g., based on delivery.getStatus()
        // For example, prevent deletion if status is IN_TRANSIT or DELIVERED.

        deliveryRepository.delete(delivery);
        // Optionally, send a notification about the deletion.
    }

    @Transactional
    @DistributedLock(keyPrefix = "delivery", keyIdentifierExpression = "#trackingId", leaseTime = 120, timeUnit = TimeUnit.SECONDS)
    public DeliveryResponseDTO updateDeliveryDetails(String trackingId, DeliveryUpdateDTO updateDTO) {
        Delivery delivery = deliveryRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Delivery not found with tracking ID: " + trackingId));

        boolean updated = false;

        if (updateDTO.getDescription() != null) {
            delivery.setDescription(updateDTO.getDescription());
            updated = true;
        }
        if (updateDTO.getRecipientName() != null) {
            delivery.setRecipientName(updateDTO.getRecipientName());
            updated = true;
        }
        if (updateDTO.getPickupLocation() != null) {
            delivery.setPickupLocation(updateDTO.getPickupLocation());
            updated = true;
        }
        if (updateDTO.getDropoffLocation() != null) {
            delivery.setDropoffLocation(updateDTO.getDropoffLocation());
            updated = true;
        }
        if (updateDTO.getRecipientMobileNumber() != null) {
            delivery.setRecipientMobileNumber(updateDTO.getRecipientMobileNumber());
            updated = true;
        }
        // Add checks for other editable fields from DeliveryUpdateDTO here
        // e.g., if (updateDTO.getScheduledTime() != null) { ... }

        if (updated) {
            delivery = deliveryRepository.save(delivery);
            // Consider sending a notification to the customer if details changed
            User customer = delivery.getCustomer();
            if (customer != null) {
                String subject = "Your Delivery Details Have Been Updated - Tracking ID: " + delivery.getTrackingId();
                String body = String.format(
                    "Hello %s,<br><br>" +
                    "The details for your delivery (Tracking ID: %s) have been updated. Here are the current details:<br><br>" +
                    "<b>Description:</b> %s<br>" +
                    "<b>Recipient Name:</b> %s<br>" +
                    "<b>Recipient Mobile:</b> %s<br>" +
                    "<b>Pickup Address:</b> %s<br>" +
                    "<b>Recipient Address (Dropoff):</b> %s<br><br>" +
                    "Thank you for using Bostman!",
                    customer.getFullName() != null ? customer.getFullName() : "Valued Customer",
                    delivery.getTrackingId(),
                    delivery.getDescription() != null ? delivery.getDescription() : "N/A",
                    delivery.getRecipientName() != null ? delivery.getRecipientName() : "N/A",
                    delivery.getRecipientMobileNumber() != null ? delivery.getRecipientMobileNumber() : "N/A",
                    delivery.getPickupLocation() != null ? delivery.getPickupLocation() : "N/A",
                    delivery.getDropoffLocation() != null ? delivery.getDropoffLocation() : "N/A"
                );
                messageProducer.sendEmailMessage(new EmailMessage(customer.getEmail(), subject, body));
            }
        }

        String driverEmail = delivery.getAssignedDriver() != null ? delivery.getAssignedDriver().getEmail() : null;
        return DeliveryResponseDTO.builder()
                .trackingId(delivery.getTrackingId())
                .pickupAddress(delivery.getPickupLocation())
                .recipientAddress(delivery.getDropoffLocation())
                .status(delivery.getStatus())
                .driverEmail(driverEmail)
                .description(delivery.getDescription())
                .recipientName(delivery.getRecipientName())
                .recipientMobileNumber(delivery.getRecipientMobileNumber())
                .build();
    }
}
