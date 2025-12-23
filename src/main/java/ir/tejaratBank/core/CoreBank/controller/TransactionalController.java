package ir.tejaratBank.core.CoreBank.controller;

import ir.tejaratBank.core.CoreBank.data.dto.TransactionRequest;
import ir.tejaratBank.core.CoreBank.data.model.Transaction;
import ir.tejaratBank.core.CoreBank.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionalController {

    private final TransactionService transactionService;

    public TransactionalController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@Valid @RequestBody TransactionRequest request) {
        transactionService.deposit(request);
        return ResponseEntity.ok(Map.of("message", "Deposit completed successfully"));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@Valid @RequestBody TransactionRequest request) {
        try {
            transactionService.withdraw(request);
            return ResponseEntity.ok(Map.of("message", "Withdrawal completed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransactionRequest request) {
        try {
            transactionService.transfer(request);
            return ResponseEntity.ok(Map.of("message", "Transfer completed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{accountId}")  // show all transaction about special account
    public ResponseEntity<?> getTransactionHistory(@PathVariable Long accountId) {
        try {
            List<Transaction> transactions = transactionService.getAccountTransactions(accountId);
            return ResponseEntity.ok(transactions);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


}
