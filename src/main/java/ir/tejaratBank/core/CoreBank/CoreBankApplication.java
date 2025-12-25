package ir.tejaratBank.core.CoreBank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
//@EnableCaching
public class CoreBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreBankApplication.class, args);
	}

}
