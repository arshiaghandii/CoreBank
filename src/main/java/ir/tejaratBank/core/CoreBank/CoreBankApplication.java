package ir.tejaratBank.core.CoreBank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
@EnableCaching
public class CoreBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreBankApplication.class, args);
	}

}
