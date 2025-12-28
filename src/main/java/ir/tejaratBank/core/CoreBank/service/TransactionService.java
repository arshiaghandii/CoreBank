package ir.tejaratBank.core.CoreBank.service;

import ir.tejaratBank.core.CoreBank.data.dto.TransactionRequest;
import ir.tejaratBank.core.CoreBank.data.model.Account;
import ir.tejaratBank.core.CoreBank.data.model.Transaction;
import ir.tejaratBank.core.CoreBank.data.repository.AccountRepository;
import ir.tejaratBank.core.CoreBank.data.repository.TransactionRepository;
import ir.tejaratBank.core.CoreBank.exception.InsufficientBalanceException;
import ir.tejaratBank.core.CoreBank.exception.ResourceNotFoundException;
import ir.tejaratBank.core.CoreBank.utils.GlobalLogger;
import jakarta.transaction.Transactional;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }


    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class,maxAttempts = 3,backoff = @Backoff(delay = 50))
    public void deposit(TransactionRequest request) {
        Account account = accountRepository.findByIdWithLock(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("حساب با شناسه مورد نظر یافت نشد."));
        doDeposit(account, request.getAmount(), request.getDescription());
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 50))
    public void withdraw(TransactionRequest request) {
        Account account = accountRepository.findByIdWithLock(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("حساب با شناسه مورد نظر یافت نشد."));
        doWithdraw(account, request.getAmount(), request.getDescription());
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void transfer(TransactionRequest request) {
        Long sourceId = request.getAccountId();
        Long targetId = request.getTargetAccountId();

        if (sourceId.equals(targetId)) {
            throw new RuntimeException("source and target accounts are the same.");
        }

        //  (Deadlock Prevention)
        Long firstId = Math.min(sourceId, targetId);
        Long secondId = Math.max(sourceId, targetId);


        Account firstAccount = accountRepository.findById(firstId)
                .orElseThrow(() -> new RuntimeException("First account not found"));
        Account secondAccount = accountRepository.findById(secondId)
                .orElseThrow(() -> new RuntimeException("Second account not found"));

        Account sourceAccount = sourceId.equals(firstId) ? firstAccount : secondAccount;
        Account targetAccount = targetId.equals(firstId) ? firstAccount : secondAccount;
        doWithdraw(sourceAccount, request.getAmount(), request.getDescription());
        doDeposit(targetAccount, request.getAmount(), "Transfer from " + sourceAccount.getAccountNumber());
    }

    private void doDeposit(Account account, BigDecimal amount, String description) {
        account.setBalance(account.getBalance().add(amount));

        saveTransaction(account, amount, Transaction.TransactionType.DEPOSIT, description);
    }

    private void doWithdraw(Account account, BigDecimal amount, String description) {

        GlobalLogger.debug(TransactionService.class,"Processing withdrawal -> AccountID: " + account.getId() +
                " | Balance: " + account.getBalance() +
                " | Amount: " + amount);

        if (account.getBalance().compareTo(amount) < 0) {
            GlobalLogger.error(TransactionService.class, "Insufficient funds for AccountID: " + account.getId(), null);
            throw new InsufficientBalanceException("Insufficient account balance.");
        }
        account.setBalance(account.getBalance().subtract(amount));

        saveTransaction(account, amount, Transaction.TransactionType.WITHDRAW, description);
    }

    private void saveTransaction(Account account, BigDecimal amount, Transaction.TransactionType type, String description) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setDescription(description);
        transactionRepository.save(transaction);
    }

    public List<Transaction> getAccountTransactions(Long accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new RuntimeException("Account " + accountId + " not found");
        }
        return transactionRepository.findByAccountId(accountId);
    }
}