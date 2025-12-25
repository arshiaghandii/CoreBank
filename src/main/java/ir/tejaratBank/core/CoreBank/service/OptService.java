package ir.tejaratBank.core.CoreBank.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OptService {

    private final StringRedisTemplate redisTemplate;

    // پیشوند کلیدها در ردیس
    private static final String OTP_PREFIX = "OTP:";
    private static final Duration OTP_TTL = Duration.ofMinutes(2);

    public OptService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }



    public String generateOtp(String nationalId) {
        String otpCode = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
        String key = OTP_PREFIX + nationalId;

        redisTemplate.opsForValue().set(key, otpCode, OTP_TTL);

        System.out.println("OTP for Customer [" + nationalId + "] is: " + otpCode);
        return otpCode;
    }

    public boolean validateOtp(String nationalId, String inputCode) {
        String key = OTP_PREFIX + nationalId;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode != null && storedCode.equals(inputCode)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }
}