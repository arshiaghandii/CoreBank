package ir.tejaratBank.core.CoreBank.controller;

import ir.tejaratBank.core.CoreBank.data.model.Loan;
import ir.tejaratBank.core.CoreBank.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestLoan(@RequestParam Long customerId,
                                         @RequestParam BigDecimal amount,
                                         @RequestParam Integer installments,
                                         @RequestParam Double interestRate) {
        try {
            Loan loan = loanService.grantLoan(customerId, amount, installments, interestRate);

            return ResponseEntity.ok(Map.of(
                    "message", "تسهیلات با موفقیت پرداخت شد",
                    "loanNumber", loan.getLoanNumber(),
                    "loanId" ,loan.getId(),
                    "principalAmount", loan.getTotalAmount(), // مبلغ اصل وام
                    "totalRepayment", loan.getRemainingAmount(), // مبلغی که باید پس دهد (با سود)
                    "startDate", loan.getStartDate()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLoan(@PathVariable Long id) {
        try {
            loanService.deleteLoan(id);
            return ResponseEntity.ok(Map.of("message", "وام با موفقیت حذف شد."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/repay")
    public ResponseEntity<?> repayLoan(@RequestParam Long loanId,
                                       @RequestParam Long sourceAccountId,
                                       @RequestParam BigDecimal amount) {
        try {
            Loan updatedLoan = loanService.repayLoan(loanId, sourceAccountId, amount);
            return ResponseEntity.ok(Map.of(
                    "message", "قسط با موفقیت پرداخت شد",
                    "remainingDebt", updatedLoan.getRemainingAmount(),
                    "paidInstallments", updatedLoan.getPaidInstallments()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}