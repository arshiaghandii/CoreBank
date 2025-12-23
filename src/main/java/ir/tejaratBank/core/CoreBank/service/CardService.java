package ir.tejaratBank.core.CoreBank.service;

import ir.tejaratBank.core.CoreBank.BankingUtils;
import ir.tejaratBank.core.CoreBank.data.model.Account;
import ir.tejaratBank.core.CoreBank.data.model.Card;
import ir.tejaratBank.core.CoreBank.data.repository.AccountRepository;
import ir.tejaratBank.core.CoreBank.data.repository.CardRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private static final String TEJARAT_BIN = "585983";

    public CardService(CardRepository cardRepository, AccountRepository accountRepository) {
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Card issueCard(Long accountId, String pin) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("حساب یافت نشد."));

        Card card = new Card();
        card.setAccount(account);
        card.setPin(pin);
        card.setActive(true);

        String cardNumber = generateValidCardNumber();
        card.setCardNumber(cardNumber);

        String cvv2 = String.valueOf(ThreadLocalRandom.current().nextInt(1000, 9999));
        card.setCvv2(cvv2);

        LocalDate expDate = LocalDate.now().plusYears(5);
        String formattedExpDate = String.format("%02d/%02d", expDate.getYear() % 100, expDate.getMonthValue());
        card.setExpireDate(formattedExpDate);

        return cardRepository.save(card);

    }

    private String generateValidCardNumber() {
        String baseNumber;
        String findCardNumber;
        do {
            long randomPart = ThreadLocalRandom.current().nextLong(100000000L, 999999999L);
            baseNumber = TEJARAT_BIN + randomPart;

            String checkDigit = BankingUtils.calculateCheckDigit(baseNumber);
            findCardNumber = baseNumber + checkDigit;
        } while (cardRepository.existsByCardNumber(findCardNumber));

        return findCardNumber;
    }
}
