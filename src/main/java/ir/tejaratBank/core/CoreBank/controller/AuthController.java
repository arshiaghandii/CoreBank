package ir.tejaratBank.core.CoreBank.controller;

import ir.tejaratBank.core.CoreBank.data.repository.CustomerRepository;
import ir.tejaratBank.core.CoreBank.service.OptService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/otp")
public class OTPController {

    private final OptService optService;
    private final RedisTemplate<String,String> redisTemplate
    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}/protocol/openid-connect/token")
    private String keycloakTokenUrl;

    @Value("${keycloak.resource:core-bank-app}")
    private String clientId;

    private static final String SESSION_DATA_PREFIX = "auth:session:";
    private static final String KC_TOKEN_PREFIX = "auth:kc_token:";

    public OTPController(OptService optService, CustomerRepository customerRepository) {
        this.optService = optService;
        this.customerRepository = customerRepository;
        this.restTemplate = new RestTemplate();
    }

    /**
    * step 1 -> sent otp to client
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@RequestParam String customerId) {
        if (!customerRepository.existsByNationalId(customerId)) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Customer not found. Please register first."));
        }

        String code = optService.generateOtp(customerId);
        return ResponseEntity.ok(Map.of(
                "message", "OTP sent successfully",
                "code", code
        ));
    }

    /**
     * step 2 -> login proxy
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestParam String customerId, @RequestParam String code) {

        boolean isValid = optService.validateOtp(customerId, code);

        if (!isValid) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired OTP"));
        }

        try {
            return loginToKeycloak(customerId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Login failed on Identity Server"));
        }
    }

    private ResponseEntity<?> loginToKeycloak(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", "core-bank-app"); // نام کلاینت در Keycloak
        map.add("grant_type", "password");
        map.add("username", username);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        return restTemplate.postForEntity(keycloakTokenUrl, request, Map.class);
    }
}