package com.bostman.controller;

import com.bostman.entity.DeliveryStatus;
import com.bostman.dto.UserResponseDTO;
import com.bostman.entity.Delivery;
import com.bostman.service.AdminService;
import com.bostman.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    @PostMapping("/assign-driver")
    public String assignDriver(@RequestParam String trackingId, @RequestParam String driverEmail) {
        return adminService.assignDriver(trackingId, driverEmail);
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam String trackingId, @RequestParam DeliveryStatus status) {
        return adminService.updateStatus(trackingId, status);
    }

    @PutMapping("/deliveries/{deliveryId}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignDriverToDelivery(@PathVariable Long deliveryId, @RequestParam Long driverId) {
        try {
            Delivery delivery = adminService.assignDriverToDelivery(deliveryId, driverId);
            return ResponseEntity.ok(delivery);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/users/drivers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getDrivers() {
        List<UserResponseDTO> drivers = userService.findUsersByRole(com.bostman.entity.Role.DRIVER);
        return ResponseEntity.ok(drivers);
    }
}
