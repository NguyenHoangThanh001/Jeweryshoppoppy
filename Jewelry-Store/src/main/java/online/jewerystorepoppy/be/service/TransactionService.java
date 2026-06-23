package online.jewerystorepoppy.be.service;


import jakarta.persistence.criteria.Predicate;
import online.jewerystorepoppy.be.entity.Orders;
import online.jewerystorepoppy.be.entity.Transaction;
import online.jewerystorepoppy.be.exception.OrderNotFoundException;
import online.jewerystorepoppy.be.model.CreateTransactionDTO;
import online.jewerystorepoppy.be.model.TransactionPageRequestDTO;
import online.jewerystorepoppy.be.model.ViewTransactionDTO;
import online.jewerystorepoppy.be.repository.OrderRepository;
import online.jewerystorepoppy.be.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//Create a transaction result from Order after staff approved the payment
//Reason, the payment gate return url is front end, so front end will handle the decision whenever or not to create the transaction result.
//For now, doesn't save to database

@Service
public class TransactionService {


    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, OrderRepository orderRepository) {
        this.transactionRepository = transactionRepository;
        this.orderRepository = orderRepository;
    }

    public ViewTransactionDTO findTransactionById(UUID transactionId){
        Transaction transaction = transactionRepository.findTransactionById(transactionId);
        if(transaction==null){
            return null;
        }

        return mapToDTO(transaction);
    }

    public void addTransaction(CreateTransactionDTO dto){

        //Look for order
        Optional<Orders> result = orderRepository.findById(dto.getOrderId());

        Orders order = result.orElseThrow(()-> new OrderNotFoundException(dto.getOrderId()));

        Transaction entity = Transaction.builder()
                .username(dto.getUsername())
                .billingAddress(dto.getBillingAddress())
                .totalMoney(dto.getTotalMoney())
                .paymentStatus(dto.getPaymentStatus())
                .GeneratedAt(LocalDateTime.now())
                .order(order)
                .build();

        transactionRepository.save(entity);
    }

    //Find range based date range or total money range, can be sort by username, must be pageable

    public Page<ViewTransactionDTO> getAllTransactionPageable(TransactionPageRequestDTO request){
        Sort sort = request.sortByUsernameAscending ?
                Sort.by("username").ascending() :
                Sort.by("username").descending();

        Pageable pageable = PageRequest.of(request.Page, request.PageSize, sort);
        Specification<Transaction> spec = buildSpecification(request);

        Page<Transaction> transactions = transactionRepository.findAll(spec,pageable);

        return transactions.map(this::mapToDTO);
    }

    private ViewTransactionDTO mapToDTO(Transaction entity){
        ViewTransactionDTO dto = ViewTransactionDTO.builder()
                .id(entity.getId().toString())
                .billingAddress(entity.getBillingAddress())
                .GeneratedAt(entity.getGeneratedAt())
                .orderId(entity.getOrder().getId())
                .paymentStatus(entity.getPaymentStatus())
                .totalMoney(entity.getTotalMoney())
                .username(entity.getUsername())
                .build();
        return dto;
    }

    private Specification<Transaction> buildSpecification(TransactionPageRequestDTO request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.between(root.get("totalMoney"),
                    request.getTotalMoneyFrom(), request.getTotalMoneyTo()));

            if (request.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("generatedAt"), request.getDateFrom()));
            }
            if (request.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("generatedAt"), request.getDateTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
