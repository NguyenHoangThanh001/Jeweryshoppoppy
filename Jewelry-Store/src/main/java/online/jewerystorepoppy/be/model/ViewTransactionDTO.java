package online.jewerystorepoppy.be.model;

import lombok.Builder;
import lombok.Data;
import online.jewerystorepoppy.be.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class ViewTransactionDTO {
    String id;
    String username;
    String billingAddress;
    double totalMoney;
    Long orderId;
    LocalDateTime GeneratedAt;
    OrderStatus paymentStatus;
}
