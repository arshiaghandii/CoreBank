package ir.tejaratBank.core.CoreBank.controller;

import ir.tejaratBank.core.CoreBank.data.model.Customer;
import ir.tejaratBank.core.CoreBank.data.repository.CustomerRepository;
import ir.tejaratBank.core.CoreBank.service.OptService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final OptService optService;
    private final StringRedisTemplate redisTemplate;
    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}/protocol/openid-connect/token")
    private String keycloakTokenUrl;

    @Value("${keycloak.resource:core-bank-app}")
    private String clientId;

    private static final String SESSION_DATA_PREFIX = "auth:session:";
    private static final String KC_TOKEN_PREFIX = "auth:kc_token:";

    public AuthController(OptService optService, StringRedisTemplate redisTemplate, CustomerRepository customerRepository) {
        this.optService = optService;
        this.redisTemplate = redisTemplate;
        this.customerRepository = customerRepository;
        this.restTemplate = new RestTemplate();
    }

    /**
     * مرحله ۱: لاگین اولیه
     * هدف: تایید یوزر/پسورد + کش کردن شماره موبایل در ردیس
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        try {
            ResponseEntity<Map> kcResponse = loginToKeycloak(username, password);
            if (!kcResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
            }

            Map<String, Object> body = kcResponse.getBody();
            String accessToken = (String) body.get("access_token");

            Customer customer = customerRepository.findByNationalId(username)
                    .orElseThrow(() -> new RuntimeException("User exists in Keycloak but not in CoreBank DB!"));

            String mobileNumber = customer.getPhoneNumber();

            String loginId = UUID.randomUUID().toString();

            // 3 min of expire
            redisTemplate.opsForValue().set(KC_TOKEN_PREFIX + loginId, accessToken, 180, TimeUnit.SECONDS);


            String sessionData = username + ":" + mobileNumber;
            redisTemplate.opsForValue().set(SESSION_DATA_PREFIX + loginId, sessionData, 180, TimeUnit.SECONDS);

            return ResponseEntity.ok(Map.of(
                    "message", "Credentials valid.",
                    "loginId", loginId,
                    "nextStep", "/auth/send-otp"
            ));

        } catch (HttpClientErrorException.Unauthorized e) {
            // === اصلاح شده: چاپ دلیل اصلی خطا از سمت Keycloak ===
            String keycloakError = e.getResponseBodyAsString();
            System.err.println("🔥 Keycloak 401 Error Body: " + keycloakError);

            return ResponseEntity.status(401).body(Map.of(
                    "message", "Login Failed",
                    "details", keycloakError // این را به فرانت/پستمن برمی‌گردانیم تا ببینیم
            ));
        } catch (HttpClientErrorException e) {
            System.err.println("Keycloak Error: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(Map.class));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Internal Login Error"));
        }
    }

    /**
     * مرحله ۲: ارسال OTP
     * نکته مهم: اینجا دیگر هیچ درخواستی به دیتابیس Postgres زده نمی‌شود.
     */
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestParam String loginId)  {
        String sessionData = redisTemplate.opsForValue().get(SESSION_DATA_PREFIX + loginId);

        if (sessionData == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Session expired."));
        }

        String[] parts = sessionData.split(":");
        String username = parts[0];
        String mobile = parts[1];
        String otpCode = optService.generateOtp(username);

        System.out.println(">>> Sending SMS to [" + mobile + "]");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println(">>> OTP code [" + otpCode + "]");
        return ResponseEntity.ok(Map.of(
                "message", "OTP sent to " + mobile,
                "debug_code", otpCode
        ));
    }

    /**
     * مرحله ۳: تایید نهایی
     * بدون نیاز به دیتابیس Postgres
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String loginId, @RequestParam String otp) {
        String sessionData = redisTemplate.opsForValue().get(SESSION_DATA_PREFIX + loginId);
        if (sessionData == null) return ResponseEntity.status(401).body(Map.of("message", "Session expired."));

        String username = sessionData.split(":")[0];

        // ۱. اعتبارسنجی با Redis
        boolean isValid = optService.validateOtp(username, otp);
        if (!isValid) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid OTP"));
        }

        // ۲. آزادسازی توکن
        String realAccessToken = redisTemplate.opsForValue().get(KC_TOKEN_PREFIX + loginId);

        // پاکسازی ردیس (اختیاری)
        redisTemplate.delete(SESSION_DATA_PREFIX + loginId);
        redisTemplate.delete(KC_TOKEN_PREFIX + loginId);

        return ResponseEntity.ok(Map.of("message", "Login Successful", "access_token", realAccessToken));
    }

    // متد Login Keycloak (بدون تغییر)
    private ResponseEntity<Map> loginToKeycloak(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("grant_type", "password");
        map.add("username", username);
        map.add("password", password);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        return restTemplate.postForEntity(keycloakTokenUrl, request, Map.class);
    }
}