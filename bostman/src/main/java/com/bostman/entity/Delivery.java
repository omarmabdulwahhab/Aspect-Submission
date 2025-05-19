package com.bostman.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackingId;

    private String pickupLocation;
    private String dropoffLocation;
    private String description;
    private String recipientName;
    private String recipientMobileNumber;

    private LocalDateTime scheduledTime;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @ManyToOne
    @JoinColumn(name = "assigned_driver_id")
    private User assignedDriver;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;
}
