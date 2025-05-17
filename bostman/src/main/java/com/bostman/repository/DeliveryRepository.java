package com.bostman.repository;

import com.bostman.entity.Delivery;
import com.bostman.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByCustomer(User user);
    Optional<Delivery> findByTrackingId(String trackingId);
}
