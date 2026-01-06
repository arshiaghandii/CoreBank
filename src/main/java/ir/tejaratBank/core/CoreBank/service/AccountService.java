package ir.tejaratBank.core.CoreBank.service;

import ir.tejaratBank.core.CoreBank.exception.ResourceNotFoundException;
import ir.tejaratBank.core.CoreBank.utils.BankingUtils;
import ir.tejaratBank.core.CoreBank.data.model.Account;
import ir.tejaratBank.core.CoreBank.data.model.Customer;
import ir.tejaratBank.core.CoreBank.data.repository.AccountRepository;
import ir.tejaratBank.core.CoreBank.data.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;


@Service
public class AccountService {

    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;

    private static final String BRANCH_CODE = "1010";    // -> SHOBE MARKAZI
    private static final String TYPE_SAVING = "001";     // -> SEPORDEH KOTAH MODAT
    private static final String TYPE_CURRENT = "002";     // -> JARI
    private static final String TYPE_DEPOSIT = "003";     // -> GHARZOL HASANE

    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Cacheable(value = "account", key = "#accountNumber")
    public Account getAccountByNumber(String accountNumber) {
        System.out.println("Fetching from database for : " + accountNumber);
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

    }

    @Transactional
    @Cacheable(value = "account", key = "#account.accountNumber")
    public void update(Account account) {
        accountRepository.save(account);
        System.out.println("Cache Evicted for : " + account.getAccountNumber());
    }

    @Transactional
    public Account createAccount(Long customerId, Account.AccountType accountType) {

        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));

        Account account = new Account();
        account.setCustomer(customer);
        account.setAccountType(accountType);
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(Account.AccountStatus.ACTIVE);
        String generatedAccountNum = generateSmartAccountNumber(accountType);
        account.setAccountNumber(generatedAccountNum);

        String iban = BankingUtils.generateIBAN(generatedAccountNum);
        account.setIban(iban);
        return accountRepository.save(account);

    }

    private String generateSmartAccountNumber(Account.AccountType type) {
        String typeCode;
        switch (type) {
            case SAVINGS -> typeCode = TYPE_SAVING;
            case CURRENT -> typeCode = TYPE_CURRENT;
            default -> typeCode = TYPE_DEPOSIT;
        }
        long serial = ThreadLocalRandom.current().nextLong(10000, 99999);
        String baseNumber = BRANCH_CODE + typeCode + serial;
        String checkDigits = BankingUtils.calculateCheckDigit(baseNumber);
        return baseNumber + checkDigits;

    }


}
