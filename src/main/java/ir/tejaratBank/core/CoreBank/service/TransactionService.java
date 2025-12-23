package ir.tejaratBank.core.CoreBank.service;

import ir.tejaratBank.core.CoreBank.data.dto.TransactionRequest;
import ir.tejaratBank.core.CoreBank.data.model.Account;
import ir.tejaratBank.core.CoreBank.data.model.Transaction;
import ir.tejaratBank.core.CoreBank.data.repository.AccountRepository;
import ir.tejaratBank.core.CoreBank.data.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
    public void deposit(TransactionRequest request) {
        Account account = accountRepository.findByIdWithLock(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("account not found"));
        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(request.getAmount());
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setDescription(request.getDescription());
        transactionRepository.save(transaction);
    }

    @Transactional
    public void withdraw(TransactionRequest request) {
        Account account = accountRepository.findByIdWithLock(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("account not found"));
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds.");
        }
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(request.getAmount());
        transaction.setType(Transaction.TransactionType.WITHDRAW);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    @Transactional
    public void transfer(TransactionRequest request) {
        Long sourceId = request.getAccountId();
        Long targetId = request.getTargetAccountId();

        if (sourceId.equals(targetId)) {
            throw new RuntimeException("source and target accounts are the same.");
        }

        if (sourceId < targetId) {
            accountRepository.findByIdWithLock(sourceId); // قفل اول
            accountRepository.findByIdWithLock(targetId); // قفل دوم
        } else {
            accountRepository.findByIdWithLock(targetId); // قفل اول
            accountRepository.findByIdWithLock(sourceId); // قفل دوم
        }


        this.withdraw(request);
        TransactionRequest depositRequest = new TransactionRequest();
        depositRequest.setAccountId(request.getTargetAccountId());
        depositRequest.setAmount(request.getAmount());
        depositRequest.setDescription("Transfer from account " + request.getAccountId());
        this.deposit(depositRequest);

    }

    public List<Transaction> getAccountTransactions(Long accountId) {

        if (!accountRepository.existsById(accountId)) {
            throw new RuntimeException("Account " + accountId + " not found");
        }
        return transactionRepository.findByAccountId(accountId);
    }
}
