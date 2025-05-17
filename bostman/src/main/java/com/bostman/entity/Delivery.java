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

    private LocalDateTime scheduledTime;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    //private String assignedDriver;
    private User assignedDriver;

    @ManyToOne
    private User customer;
}
