package online.jewerystorepoppy.be.entity;

import online.jewerystorepoppy.be.enums.OrderStatus;

import java.time.LocalDateTime;

public class Transaction {
    long Id;
    long OrderId;
    LocalDateTime GeneratedAt; //Generate this entity after payment
    OrderStatus paymentStatus;
}
