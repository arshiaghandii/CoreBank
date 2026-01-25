package ir.tejaratBank.core.CoreBank.controller;

import ir.tejaratBank.core.CoreBank.data.model.Account;
import ir.tejaratBank.core.CoreBank.data.repository.AccountRepository;
import ir.tejaratBank.core.CoreBank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;


    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAccount(@RequestParam Long customerId,
                                           @RequestParam Account.AccountType type) {
        try {
            Account newAcc = accountService.createAccount(customerId, type);
            return ResponseEntity.ok(Map.of(
                    "message", "Account created successfully",
                    "accountNumber", newAcc.getAccountNumber(),
                    "iban", newAcc.getIban(),
                    "balance", newAcc.getBalance()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("ERROR", e.getMessage()));
        }

    }

    @GetMapping("/my-details")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getMyDetails(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        return ResponseEntity.ok("Hello " + username);
    }

    @GetMapping("/test-generate")
    public ResponseEntity<String> testGeneration() {
        return ResponseEntity.ok("Controller is working!");
    }


}
