package com.bostman.controller;

import com.bostman.dto.DeliveryResponseDTO;
import com.bostman.entity.User;
import com.bostman.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DeliveryService deliveryService;

    @GetMapping("/deliveries")
    @PreAuthorize("hasRole('DRIVER')")
    public List<DeliveryResponseDTO> getAssignedDeliveries(@AuthenticationPrincipal User driver) {
        return deliveryService.getDeliveriesByDriver(driver.getEmail());
    }

    @PostMapping("/complete")
    @PreAuthorize("hasRole('DRIVER')")
    public String completeDelivery(@RequestParam String trackingId, @AuthenticationPrincipal User driver) {
        return deliveryService.markAsDelivered(trackingId, driver.getEmail());
    }
}
