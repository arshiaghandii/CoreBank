package ir.tejaratBank.core.CoreBank.service;

import ir.tejaratBank.core.CoreBank.data.repository.AccountRepository;
import ir.tejaratBank.core.CoreBank.data.repository.CustomerRepository;
import ir.tejaratBank.core.CoreBank.data.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReportService {

    private final AccountRepository accountRepository;
    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;

    public ReportService(AccountRepository accountRepository,
                         LoanRepository loanRepository,
                         CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.loanRepository = loanRepository;
        this.customerRepository = customerRepository;
    }

    public Map<String, Object> getGeneralReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("totalCustomers", customerRepository.count());
        BigDecimal liquidity = accountRepository.getTotalBankLiquidity();
        report.put("totalLiquidity", liquidity != null ? liquidity : BigDecimal.ZERO);
        BigDecimal totalLoans = loanRepository.getTotalLoansAmount();
        report.put("totalLoansGranted", totalLoans != null ? totalLoans : BigDecimal.ZERO);
        report.put("totalLoanCount", loanRepository.count());
        return report;
    }
}