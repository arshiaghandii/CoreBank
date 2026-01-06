package ir.tejaratBank.core.CoreBank;

import ir.tejaratBank.core.CoreBank.data.model.Account;
import ir.tejaratBank.core.CoreBank.data.model.Customer;
import ir.tejaratBank.core.CoreBank.data.repository.AccountRepository;
import ir.tejaratBank.core.CoreBank.data.repository.CustomerRepository;
import ir.tejaratBank.core.CoreBank.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
@EnableCaching
public class CoreBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreBankApplication.class, args);
	}

    @Bean
    public CommandLineRunner testAccountService(AccountService accountService, CustomerRepository customerRepository) {
        return args -> {
            // ۱. پیدا کردن مشتری (همونی که توی دیتابیس داریم)
            // فرض بر اینه که مشتری با کد ملی "1234567890" از قبل ساخته شده
            Customer customer = customerRepository.findAll().stream()
                    .filter(c -> "1234567890".equals(c.getNationalId()))
                    .findFirst()
                    .orElse(null);

            if (customer != null) {
                System.out.println("🦅 Customer Found: " + customer.getFirstName() + " " + customer.getLastName());

                // ۲. افتتاح حساب جدید با سرویس (تست لاجیک تولید شماره حساب)
                try {
                    Account newAccount = accountService.createAccount(customer.getId(), Account.AccountType.SAVINGS);

                    // ۳. نمایش خروجی برای تأیید مهندس
                    System.out.println("✅ New Account Created via Service:");
                    System.out.println("   -------------------------------------------------");
                    System.out.println("   👤 Owner: " + newAccount.getCustomer().getFirstName());
                    System.out.println("   🔢 Account Type: " + newAccount.getAccountType());
                    System.out.println("   💳 Smart Account Num: " + newAccount.getAccountNumber());
                    System.out.println("   🌍 Generated IBAN:    " + newAccount.getIban());
                    System.out.println("   -------------------------------------------------");

                } catch (Exception e) {
                    System.err.println("❌ Error creating account: " + e.getMessage());
                }
            } else {
                System.out.println("⚠️ Warning: No customer found to test AccountService!");
            }
        };
    }
}
