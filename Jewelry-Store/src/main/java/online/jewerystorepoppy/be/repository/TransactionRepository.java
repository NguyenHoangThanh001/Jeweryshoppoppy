package online.jewerystorepoppy.be.repository;

import online.jewerystorepoppy.be.entity.Size;
import online.jewerystorepoppy.be.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository  extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {
    Transaction findTransactionById(UUID id);

}
