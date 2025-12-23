package ir.tejaratBank.core.CoreBank.data.repository;

import ir.tejaratBank.core.CoreBank.data.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan,Long> {
    List<Loan> findByCustomerId(Long customerId);

    // پیدا کردن وام با شماره پرونده/قرارداد
    Optional<Loan> findByLoanNumber(String loanNumber);

    @Query("SELECT SUM(l.totalAmount) FROM Loan l")
    BigDecimal getTotalLoansAmount();
}
