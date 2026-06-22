package online.jewerystorepoppy.be.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import online.jewerystorepoppy.be.entity.Orders;
import online.jewerystorepoppy.be.enums.OrderStatus;
import online.jewerystorepoppy.be.model.OrderRequest;
import online.jewerystorepoppy.be.model.RechargeRequestDTO;
import online.jewerystorepoppy.be.service.OrderService;
import online.jewerystorepoppy.be.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/order")
@SecurityRequirement(name = "api")
public class OrderAPI {

    @Autowired
    OrderService orderService;

    @Autowired
    WalletService walletService;


    @PostMapping
    public ResponseEntity createOrder(@RequestBody OrderRequest orderRequest) {
        Orders orders = orderService.createOrder(orderRequest);
        return ResponseEntity.ok(orders);
    }

    @GetMapping
    public ResponseEntity getOrder(@RequestParam(required = false) String phone) {
        List<Orders> orders = orderService.getOrder(phone);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("payment")
    public ResponseEntity payment(@RequestBody OrderRequest orderRequest) throws Exception {
        String url = orderService.createUrl(orderRequest);
        return ResponseEntity.ok(url);
    }

    @PatchMapping("/{id}")
    public ResponseEntity updateStatusOrder(@PathVariable long id, @RequestParam OrderStatus orderStatus) {
        Orders order = orderService.updateStatusOrder(id, orderStatus);
        return ResponseEntity.ok(order);
    }
}
