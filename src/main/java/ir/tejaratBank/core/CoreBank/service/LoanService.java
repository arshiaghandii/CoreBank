package ir.tejaratBank.core.CoreBank.service;

import ir.tejaratBank.core.CoreBank.data.dto.TransactionRequest;
import ir.tejaratBank.core.CoreBank.data.model.Customer;
import ir.tejaratBank.core.CoreBank.data.model.Loan;
import ir.tejaratBank.core.CoreBank.data.repository.AccountRepository;
import ir.tejaratBank.core.CoreBank.data.repository.CustomerRepository;
import ir.tejaratBank.core.CoreBank.data.repository.LoanRepository;
import ir.tejaratBank.core.CoreBank.exception.BusinessLogicException;
import ir.tejaratBank.core.CoreBank.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom; // <--- این ایمپورت را اضافه کنید

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final TransactionService transactionService;

    public LoanService(LoanRepository loanRepository,
                       CustomerRepository customerRepository,
                       AccountRepository accountRepository,
                       TransactionService transactionService) {
        this.loanRepository = loanRepository;
        this.customerRepository = customerRepository;
        this.transactionService = transactionService;
    }

    @Transactional
    public Loan grantLoan(Long customerId, BigDecimal amount, Integer installments, Double interestRate) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("مشتری یافت نشد."));

        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setTotalAmount(amount);
        loan.setInterestRate(interestRate);
        loan.setTotalInstallments(installments);
        loan.setPaidInstallments(0);
        loan.setStartDate(LocalDate.now());

        BigDecimal interestFactor = BigDecimal.valueOf(1 + (interestRate / 100));
        BigDecimal repaymentAmount = amount.multiply(interestFactor);
        loan.setRemainingAmount(repaymentAmount);

        long randomNum = ThreadLocalRandom.current().nextLong(100000000000000L, 999999999999999L);
        String loanNumber = "L-" + randomNum;

        loan.setLoanNumber(loanNumber);

        return loanRepository.save(loan);
    }

    @Transactional
    public void deleteLoan(Long loanId) {
        if (!loanRepository.existsById(loanId)) {
            throw new RuntimeException("وام با شناسه " + loanId + " یافت نشد.");
        }
        loanRepository.deleteById(loanId);
    }

    @Transactional
    public Loan repayLoan(Long loanId, Long sourceAccountId, BigDecimal amount) {
        // ۱. پیدا کردن وام
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("وام یافت نشد."));

        if (loan.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessLogicException("این وام قبلاً تسویه شده است.");}

        // ۲. بررسی اینکه وام تسویه شده یا نه
        if (loan.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("این وام قبلاً تسویه شده است.");
        }

        TransactionRequest withdrawRequest = new TransactionRequest();
        withdrawRequest.setAccountId(sourceAccountId);
        withdrawRequest.setAmount(amount);
        withdrawRequest.setDescription("Payment for Loan: " + loan.getLoanNumber());

        transactionService.withdraw(withdrawRequest);

        BigDecimal newRemaining = loan.getRemainingAmount().subtract(amount);
        if (newRemaining.compareTo(BigDecimal.ZERO) < 0) {
            newRemaining = BigDecimal.ZERO;
        }

        loan.setRemainingAmount(newRemaining);
        loan.setPaidInstallments(loan.getPaidInstallments() + 1);

        return loanRepository.save(loan);
    }
}