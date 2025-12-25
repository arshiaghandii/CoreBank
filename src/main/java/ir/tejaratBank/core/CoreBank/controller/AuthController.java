package ir.tejaratBank.core.CoreBank.controller;

import ir.tejaratBank.core.CoreBank.data.repository.CustomerRepository;
import ir.tejaratBank.core.CoreBank.service.OptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final OptService optService;
    private final CustomerRepository customerRepository;

    public AuthController(OptService optService, CustomerRepository customerRepository) {
        this.optService = optService;
        this.customerRepository = customerRepository;
    }
    @PostMapping("/send-otp") // این خط جا افتاده بود
    public ResponseEntity<?> sendOtp(@RequestParam String customerId) {
        if (!customerRepository.existsByNationalId(customerId)) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Customer not found with this ID"));
        }

        String code =optService.generateOtp(customerId);
        return ResponseEntity.ok(Map.of("message","OTP Sent to registered mobile", "debug_code", code));
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String customerId, @RequestParam String code) {
        boolean isValid = optService.validateOtp(customerId, code);

        if (isValid) {
            return ResponseEntity.ok(Map.of("message", "Login Successful", "token", "FAKE-JWT-TOKEN"));
        } else {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid OTP"));
        }
    }
}
