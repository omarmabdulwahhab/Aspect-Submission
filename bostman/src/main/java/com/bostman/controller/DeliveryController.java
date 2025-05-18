package com.bostman.controller;

import com.bostman.dto.DeliveryRequestDTO;
import com.bostman.dto.DeliveryResponseDTO;
import com.bostman.dto.DeliveryStatusUpdateDTO;
import com.bostman.entity.User;
import com.bostman.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public String schedule(@RequestBody DeliveryRequestDTO dto, @AuthenticationPrincipal User user) {
        return deliveryService.createDelivery(dto, user.getEmail());
    }

    @GetMapping
    public List<DeliveryResponseDTO> myDeliveries(@AuthenticationPrincipal User user) {
        return deliveryService.getMyDeliveries(user.getEmail());
    }

    @GetMapping("/{trackingId}")
    public DeliveryResponseDTO getOne(@PathVariable String trackingId) {
        return deliveryService.getByTrackingId(trackingId);
    }

    @PatchMapping("/{trackingId}/status")
    public DeliveryResponseDTO updateStatus(@PathVariable String trackingId,
                                            @RequestBody DeliveryStatusUpdateDTO statusUpdateDTO,
                                            @AuthenticationPrincipal User user) {
        // user available for future authorization checks
        return deliveryService.updateDeliveryStatus(trackingId, statusUpdateDTO.getNewStatus());
    }
    @GetMapping("/my-deliveries")
    public ResponseEntity<List<DeliveryResponseDTO>> getMyDeliveries(@AuthenticationPrincipal User user) {
        List<DeliveryResponseDTO> deliveries = deliveryService.getMyDeliveries(user.getEmail());
        return ResponseEntity.ok(deliveries);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public List<DeliveryResponseDTO> allDeliveries() {
        return deliveryService.getAllDeliveries();
    }


}
