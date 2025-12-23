package ir.tejaratBank.core.CoreBank.data.repository;

import ir.tejaratBank.core.CoreBank.data.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    boolean existsByCardNumber(String cardNumber);

    // پیدا کردن تمام کارت‌های متصل به یک حساب خاص
    List<Card> findByAccountId(Long accountId);

    // پیدا کردن کارت‌های فعال یک حساب
    List<Card> findByAccountIdAndIsActiveTrue(Long accountId);
}
