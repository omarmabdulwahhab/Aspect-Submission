package com.bostman.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Size;
// Potentially add other validation annotations as needed, e.g., @Future for scheduledTime
// import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryUpdateDTO {

    @Size(max = 255, message = "Pickup address cannot exceed 255 characters")
    private String pickupAddress; // Corresponds to pickupLocation in Entity

    @Size(max = 255, message = "Recipient address cannot exceed 255 characters")
    private String recipientAddress; // Corresponds to dropoffLocation in Entity

    @Size(max = 100, message = "Recipient name cannot exceed 100 characters")
    private String recipientName;

    @Size(max = 20, message = "Recipient phone cannot exceed 20 characters")
    // Consider adding @Pattern for phone number format if needed
    private String recipientPhone;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    // Optional: If scheduled time is editable
    // @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // Example format
    // private LocalDateTime scheduledTime;

    // Add any other fields you want to be editable
} 