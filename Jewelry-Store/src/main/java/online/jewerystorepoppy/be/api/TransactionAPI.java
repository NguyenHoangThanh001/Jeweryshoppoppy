package online.jewerystorepoppy.be.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import online.jewerystorepoppy.be.entity.Orders;
import online.jewerystorepoppy.be.model.CreateTransactionDTO;
import online.jewerystorepoppy.be.model.OrderRequest;
import online.jewerystorepoppy.be.model.TransactionPageRequestDTO;
import online.jewerystorepoppy.be.model.ViewTransactionDTO;
import online.jewerystorepoppy.be.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/transaction")
@SecurityRequirement(name = "api")
public class TransactionAPI {

    private final TransactionService transactionService;

    @Autowired
    public TransactionAPI(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity createTransaction(@RequestBody CreateTransactionDTO request) {
        transactionService.addTransaction(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{id}")
    public ResponseEntity<ViewTransactionDTO> getTransactionById(@RequestParam(required = false) UUID id) {
        ViewTransactionDTO response = transactionService.findTransactionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<ViewTransactionDTO>> getAllTransaction(@RequestBody TransactionPageRequestDTO request){
        Page<ViewTransactionDTO> response = transactionService.getAllTransactionPageable(request);
        return ResponseEntity.ok(response);
    }
}
