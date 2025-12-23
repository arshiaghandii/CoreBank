package ir.tejaratBank.core.CoreBank.controller;

import ir.tejaratBank.core.CoreBank.data.model.Card;
import ir.tejaratBank.core.CoreBank.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/issue")
    public ResponseEntity<?> issueCard(@RequestParam Long accountId,
                                       @RequestParam String pin) {
        try {
            Card newCard = cardService.issueCard(accountId, pin);
            return ResponseEntity.ok(Map.of(
                    "message", "Created new card successfully!",
                    "cardNumber", newCard.getCardNumber(),
                    "cvv2", newCard.getCvv2(),
                    "expireDate", newCard.getExpireDate()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}