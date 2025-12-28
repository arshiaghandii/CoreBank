package ir.tejaratBank.core.CoreBank;

import ir.tejaratBank.core.CoreBank.data.dto.TransactionRequest;
import ir.tejaratBank.core.CoreBank.data.model.Account;
import ir.tejaratBank.core.CoreBank.data.model.Customer;
import ir.tejaratBank.core.CoreBank.data.repository.AccountRepository;
import ir.tejaratBank.core.CoreBank.data.repository.CustomerRepository;
import ir.tejaratBank.core.CoreBank.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CoreBankApplicationTests {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testConcurrentWithdrawals() throws InterruptedException {
        // ۱. آماده‌سازی داده‌های اولیه (Setup)
        Customer customer = new Customer();
        customer.setFirstName("Arshia");
        customer.setLastName("Bolat");
        customer.setBirthDate(LocalDate.of(2000, 1, 1)); // <--- فیکس باگ: تاریخ تولد اضافه شد
        customer.setNationalId("1214575890");
        customerRepository.save(customer);

        Account account = new Account();
        account.setAccountNumber("IR099000");
        account.setBalance(new BigDecimal("10000")); // موجودی اولیه: ۱۰,۰۰۰
        account.setCustomer(customer);
        account.setAccountType(Account.AccountType.CURRENT);
        account.setStatus(Account.AccountStatus.ACTIVE);
        accountRepository.save(account);

        Long accountId = account.getId();

        // ۲. سناریوی حمله: ۱۰ نفر همزمان نفری ۱۰۰۰ تا برداشت می‌کنن
        int numberOfThreads = 1000;
        BigDecimal withdrawAmount = new BigDecimal("1000");

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        System.out.println(">>> STARTING CONCURRENCY TEST: 10 Threads Attacking...");

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    TransactionRequest request = new TransactionRequest();
                    request.setAccountId(accountId);
                    request.setAmount(withdrawAmount);
                    request.setDescription("Concurrent Attack");

                    transactionService.withdraw(request);
                } catch (Exception e) {
                    System.err.println("Transaction failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        // صبر می‌کنیم تا همه تردها کارشون تموم شه
        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        // ۳. بررسی نتیجه (Verification)
        Account updatedAccount = accountRepository.findById(accountId).orElseThrow();
        System.out.println(">>> FINAL BALANCE: " + updatedAccount.getBalance());

        // اگر ۱۰ نفر نفری ۱۰۰۰ تا بردارن، ۱۰,۰۰۰ - ۱۰,۰۰۰ = ۰
        // اگه Optimistic Locking کار نکنه، اینجا عدد غیر صفر می‌بینی!
        assertEquals(0, updatedAccount.getBalance().compareTo(BigDecimal.ZERO));
    }
}