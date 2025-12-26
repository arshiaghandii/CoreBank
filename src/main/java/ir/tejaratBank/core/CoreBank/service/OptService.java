package ir.tejaratBank.core.CoreBank.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
public class OptService {

    private final StringRedisTemplate redisTemplate;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String OTP_PREFIX = "OTP:";
    private static final Duration OTP_TTL = Duration.ofMinutes(2);


    public OptService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public String generateOtp(String nationalId) {
        String key = OTP_PREFIX + nationalId;


        int randomInt = secureRandom.nextInt(1000000);
        String otpCode = String.format("%06d", randomInt);

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