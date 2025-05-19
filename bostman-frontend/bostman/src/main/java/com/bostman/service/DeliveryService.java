import com.bostman.dto.DeliveryUpdateDTO;

public interface DeliveryService {
    String createDelivery(DeliveryRequestDTO request, String email);
    List<DeliveryResponseDTO> getMyDeliveries(String email);
    List<DeliveryResponseDTO> getAllDeliveries();
    DeliveryResponseDTO getDeliveryByTrackingId(String trackingId);
    DeliveryResponseDTO updateDeliveryStatus(String trackingId, DeliveryStatus newStatus); // User cancelling or driver/admin updating
    DeliveryResponseDTO updateDeliveryDetails(String trackingId, DeliveryUpdateDTO deliveryUpdateDTO, String userEmail);
    void deleteDelivery(String trackingId, String userEmail);
} 