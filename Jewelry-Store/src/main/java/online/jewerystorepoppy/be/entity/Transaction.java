package online.jewerystorepoppy.be.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import online.jewerystorepoppy.be.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    String username;
    String billingAddress;
    double totalMoney;
    LocalDateTime GeneratedAt; //Generated upon creation
    OrderStatus paymentStatus;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    Orders order;   // One Order -> Many Transactions
}
