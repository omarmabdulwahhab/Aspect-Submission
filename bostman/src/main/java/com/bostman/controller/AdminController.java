package com.bostman.controller;

import com.bostman.entity.DeliveryStatus;
import com.bostman.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/assign-driver")
    public String assignDriver(@RequestParam String trackingId, @RequestParam String driverEmail) {
        return adminService.assignDriver(trackingId, driverEmail);
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam String trackingId, @RequestParam DeliveryStatus status) {
        return adminService.updateStatus(trackingId, status);
    }
}
