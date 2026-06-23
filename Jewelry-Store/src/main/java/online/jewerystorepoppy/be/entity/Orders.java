package online.jewerystorepoppy.be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import online.jewerystorepoppy.be.enums.OrderStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    Date createAt;
    float totalAmount;
    OrderStatus status;
    String description;
    int point = 0;

    @ManyToOne
    @JoinColumn(name = "account_id")
    Account createBy;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    Account customer;

    @OneToMany(mappedBy = "order")
    @JsonIgnore
    List<Voucher> vouchers = new ArrayList<>();

    @ManyToOne()
    @JsonIgnore
    @JoinColumn(name = "shift_id")
    Shift shift;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "order")
    List<Transaction> transactions;
}
