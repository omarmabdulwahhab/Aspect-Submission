package com.bostman.repository;

import com.bostman.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByCustomer(String user);
    Optional<Delivery> findByTrackingId(String trackingId);
}
