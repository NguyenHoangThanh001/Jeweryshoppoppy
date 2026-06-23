package online.jewerystorepoppy.be.service;

import online.jewerystorepoppy.be.entity.*;
import online.jewerystorepoppy.be.enums.GuaranteeStatus;
import online.jewerystorepoppy.be.enums.OrderStatus;
import online.jewerystorepoppy.be.exception.AuthException;
import online.jewerystorepoppy.be.model.OrderDetailRequest;
import online.jewerystorepoppy.be.model.OrderRequest;
import online.jewerystorepoppy.be.repository.*;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    SizeRepository sizeRepository;

    public Orders createOrder(OrderRequest orderRequest) {
        float discount = 0;
        Orders order = new Orders();
        Account customer = authenticationRepository.findAccountById(orderRequest.getCustomerId());
        Voucher voucher = voucherRepository.findVoucherById(orderRequest.getVoucherId());
        long totalAmount = 0;
        List<OrderDetail> orderDetails = new ArrayList<>();
        Account account = authenticationRepository.findAccountByEmailOrPhone(authenticationService.getCurrentAccount().getEmail(), authenticationService.getCurrentAccount().getPhone());
        order.setCreateAt(new Date());
        order.setCreateBy(account);
        order.setStatus(OrderStatus.NOT_YET_PAYMENT);
        order.setOrderDetails(orderDetails);
        order.setCustomer(customer);
        order.getVouchers().add(voucher);
        order.setPoint(orderRequest.getPoint());
        if (customer != null) {
            customer.getOrders().add(order);
        }


        for (OrderDetailRequest orderDetailRequest : orderRequest.getOrderDetailRequests()) {
            OrderDetail orderDetail = new OrderDetail();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.YEAR, 1);
            Size size = sizeRepository.findSizeById(orderDetailRequest.getSizeId());

            Guarantee guarantee = new Guarantee();
            guarantee.setStatus(GuaranteeStatus.ACTIVE);
            guarantee.setOrderDetail(orderDetail);
            guarantee.setStartAt(new Date());
            guarantee.setEndAt(calendar.getTime());

            Product product = productRepository.findById(orderDetailRequest.getProductId()).get();
            if (product.getQuantity() < orderDetailRequest.getQuantity()) {
                throw new AuthException("Don't have available for sale!");
            } else {
                product.setQuantity(product.getQuantity() - orderDetailRequest.getQuantity());
            }
            size.getOrderDetails().add(orderDetail);
            orderDetail.setSize(size);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(orderDetailRequest.getQuantity());
            orderDetail.setOrder(order);
            orderDetail.setGuarantee(guarantee);
            orderDetails.add(orderDetail);
            totalAmount += product.getPrice() * orderDetailRequest.getQuantity();
        }

        if (voucher != null) {
            discount = totalAmount * voucher.getValue() / 100;
            voucher.setOrder(order);
        }

        if (order.getPoint() > 0) {
            discount += totalAmount * order.getPoint() / 100;
        }

        order.setTotalAmount(totalAmount - discount);
        return orderRepository.save(order);
    }

    public Orders updateStatusOrder(long orderId, OrderStatus orderStatus) {
        Orders orders = orderRepository.findById(orderId).get();

        if (orderStatus == OrderStatus.PAID && orders.getStatus() != OrderStatus.PAID) {
            orders.setStatus(orderStatus);
            Account account = orders.getCustomer();
            if (orders.getPoint() > 0) {
                account.setPoint(account.getPoint() - orders.getPoint());
            }
            int point = calculatePoints(orders.getTotalAmount());
            if (point > 30) point = 30;
            account.setPoint(account.getPoint() + point);
            authenticationRepository.save(account);
        }

        return orderRepository.save(orders);
    }

    public static int calculatePoints(float money) {
        int points = (int) (money / 1000000);
        return points;
    }

    /*
    public String createUrl(OrderRequest orderRequest) throws NoSuchAlgorithmException, InvalidKeyException, Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime createDate = LocalDateTime.now();
        String formattedCreateDate = createDate.format(formatter);
        Orders order = createOrder(orderRequest);
        String orderId = UUID.randomUUID().toString().substring(0, 6);

        String tmnCode = "4BV6NQ0X";
        String secretKey = "EWWARKQXLRETERG5AHWD07ZITOETOV12";
        String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        String returnUrl = "http://jewerystorepoppy.online/staff/pos?orderId=" + order.getId();
        String returnUrl = "http://localhost:5173/staff/pos?orderId=" + order.getId();

        String currCode = "VND";
        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", tmnCode);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_CurrCode", currCode);
        vnpParams.put("vnp_TxnRef", orderId);
        vnpParams.put("vnp_OrderInfo", "Thanh toan cho ma GD: " + orderId);
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Amount", (int) order.getTotalAmount() + "00");
        vnpParams.put("vnp_ReturnUrl", returnUrl);
        vnpParams.put("vnp_CreateDate", formattedCreateDate);
        vnpParams.put("vnp_IpAddr", "167.99.74.201");

        StringBuilder signDataBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            signDataBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            signDataBuilder.append("=");
            signDataBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            signDataBuilder.append("&");
        }
        signDataBuilder.deleteCharAt(signDataBuilder.length() - 1); // Remove last '&'

        String signData = signDataBuilder.toString();
        String signed = generateHMAC(secretKey, signData);

        vnpParams.put("vnp_SecureHash", signed);

        StringBuilder urlBuilder = new StringBuilder(vnpUrl);
        urlBuilder.append("?");
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            urlBuilder.append("=");
            urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            urlBuilder.append("&");
        }
        urlBuilder.deleteCharAt(urlBuilder.length() - 1); // Remove last '&'

        return urlBuilder.toString();
    }

    private String generateHMAC(String secretKey, String signData) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmacSha512.init(keySpec);
        byte[] hmacBytes = hmacSha512.doFinal(signData.getBytes(StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        for (byte b : hmacBytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

   */


    public List<Orders> getOrder(String phone) {
        List<Orders> orders;
        if (phone != null) {
            orders = orderRepository.findOrdersByCustomerPhoneContaining(phone);
        } else {
            orders = orderRepository.findAll();
        }
        return orders.stream().sorted(Comparator.comparing(Orders::getCreateAt).reversed()).toList();
    }
}
