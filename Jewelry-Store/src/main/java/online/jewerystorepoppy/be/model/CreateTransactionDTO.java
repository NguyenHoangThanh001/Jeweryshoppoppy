package online.jewerystorepoppy.be.model;

import lombok.Data;
import online.jewerystorepoppy.be.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateTransactionDTO {
    Long orderId;
    String username;
    String billingAddress;
    double totalMoney;
    OrderStatus paymentStatus;
}
