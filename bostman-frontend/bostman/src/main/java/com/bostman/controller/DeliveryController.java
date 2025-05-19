import com.bostman.dto.DeliveryUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;

@PutMapping("/{trackingId}")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')") // Or specific authority check
public ResponseEntity<DeliveryResponseDTO> updateDeliveryDetails(
        @PathVariable String trackingId,
        @Valid @RequestBody DeliveryUpdateDTO deliveryUpdateDTO,
        @AuthenticationPrincipal String userEmail) { // Or use UserDetails and getUsername()
    DeliveryResponseDTO updatedDelivery = deliveryService.updateDeliveryDetails(trackingId, deliveryUpdateDTO, userEmail);
    return ResponseEntity.ok(updatedDelivery);
} 