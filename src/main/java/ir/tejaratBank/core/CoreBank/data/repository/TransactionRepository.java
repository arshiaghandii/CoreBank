package ir.tejaratBank.core.CoreBank.data.repository;

import ir.tejaratBank.core.CoreBank.data.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // لیست تراکنش‌های یک حساب (مثلاً برای صورتحساب ۱۰ گردش آخر)
    List<Transaction> findByAccountId(Long accountId);

    // پیدا کردن با شماره پیگیری
    List<Transaction> findByTrackingCode(String trackingCode);

    // پیدا کردن تراکنش‌های واریزی یک حساب خاص
    List<Transaction> findByAccountIdAndType(Long accountId, Transaction.TransactionType type);
}
