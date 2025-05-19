import com.bostman.dto.DeliveryUpdateDTO;
import com.bostman.entity.Role;
import org.springframework.security.access.AccessDeniedException;

@Override
public DeliveryResponseDTO updateDeliveryDetails(String trackingId, DeliveryUpdateDTO deliveryUpdateDTO, String userEmail) {
    Delivery delivery = deliveryRepository.findByTrackingId(trackingId)
            .orElseThrow(() -> new RuntimeException("Delivery not found with tracking ID: " + trackingId));

    User currentUser = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));

    // Authorization: Only owner or ADMIN can edit
    boolean isAdmin = currentUser.getRoles().stream().anyMatch(role -> role.getName().equals(Role.RoleName.ADMIN));
    if (!delivery.getCustomer().getId().equals(currentUser.getId()) && !isAdmin) {
        throw new AccessDeniedException("You are not authorized to update this delivery.");
    }

    // Business Rule: Only allow edits if status is SCHEDULED (or other early statuses)
    if (delivery.getStatus() != DeliveryStatus.SCHEDULED && delivery.getStatus() != DeliveryStatus.PENDING_PICKUP) { // Example statuses
         // Admins might be allowed to edit in more states, this logic can be expanded
        if (!isAdmin) {
             throw new IllegalStateException("Delivery cannot be edited when its status is " + delivery.getStatus());
        }
        // If admin, maybe log that an edit is happening on a non-standard status delivery
        System.out.println("Admin " + userEmail + " is editing a delivery with status: " + delivery.getStatus());
    }

    // Update fields if provided in DTO
    if (deliveryUpdateDTO.getPickupAddress() != null && !deliveryUpdateDTO.getPickupAddress().isEmpty()) {
        delivery.setPickupLocation(deliveryUpdateDTO.getPickupAddress());
    }
    if (deliveryUpdateDTO.getRecipientAddress() != null && !deliveryUpdateDTO.getRecipientAddress().isEmpty()) {
        delivery.setDropoffLocation(deliveryUpdateDTO.getRecipientAddress());
    }
    if (deliveryUpdateDTO.getRecipientName() != null && !deliveryUpdateDTO.getRecipientName().isEmpty()) {
        delivery.setRecipientName(deliveryUpdateDTO.getRecipientName());
    }
    if (deliveryUpdateDTO.getRecipientPhone() != null && !deliveryUpdateDTO.getRecipientPhone().isEmpty()) {
        delivery.setRecipientPhone(deliveryUpdateDTO.getRecipientPhone());
    }
    if (deliveryUpdateDTO.getDescription() != null) { // Allow empty string to clear description
        delivery.setDescription(deliveryUpdateDTO.getDescription());
    }
    // if (deliveryUpdateDTO.getScheduledTime() != null) {
    //    delivery.setScheduledTime(deliveryUpdateDTO.getScheduledTime());
    // }

    Delivery updatedDelivery = deliveryRepository.save(delivery);

    // Send notification if needed (e.g., "Your delivery details were updated") - Skipped for brevity

    return deliveryToDeliveryResponseDTO(updatedDelivery);
}

// Make sure you have a method like this or adapt it:
private DeliveryResponseDTO deliveryToDeliveryResponseDTO(Delivery delivery) {
    String driverEmail = delivery.getAssignedDriver() != null ? delivery.getAssignedDriver().getEmail() : null;
    String customerEmail = delivery.getCustomer() != null ? delivery.getCustomer().getEmail() : null;
    return DeliveryResponseDTO.builder()
            .trackingId(delivery.getTrackingId())
            .pickupAddress(delivery.getPickupLocation())
            .recipientAddress(delivery.getDropoffLocation())
            .status(delivery.getStatus())
            .description(delivery.getDescription())
            .recipientName(delivery.getRecipientName())
            .recipientPhone(delivery.getRecipientPhone()) // Assuming recipientPhone is in ResponseDTO
            // .scheduledTime(delivery.getScheduledTime()) // Assuming scheduledTime is in ResponseDTO
            .customerEmail(customerEmail) // Add if not present and needed
            .driverEmail(driverEmail)
            .build();
} 