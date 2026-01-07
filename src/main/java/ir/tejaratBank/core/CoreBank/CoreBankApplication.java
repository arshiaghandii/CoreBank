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

import java.time.LocalDate;

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
            String targetNationalId = "1234567890";

            // ۱. تلاش برای پیدا کردن مشتری
            Customer customer = customerRepository.findByNationalId(targetNationalId)
                    .orElse(null);

            // ۲. اگر نبود، بسازش (Self-Healing Logic)
            if (customer == null) {
                System.out.println("⚠️ Customer not found. Creating a new one...");
                Customer newCustomer = new Customer();
                newCustomer.setFirstName("Arshia");
                newCustomer.setLastName("Ghandi");
                newCustomer.setNationalId(targetNationalId);
                newCustomer.setPhoneNumber("09120000000");
                newCustomer.setBirthDate(LocalDate.of(1384, 1, 1));

                customer = customerRepository.save(newCustomer); // <--- ذخیره در دیتابیس
                System.out.println("✅ Customer Created and Saved: " + customer.getId());
            } else {
                System.out.println("🦅 Customer Found: " + customer.getFirstName() + " " + customer.getLastName());
            }

            // ۳. حالا که مطمئنیم مشتری هست، حساب باز کن
            try {
                Account newAccount = accountService.createAccount(customer.getId(), Account.AccountType.SAVINGS);

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
        };
    }
}