package ir.tejaratBank.core.CoreBank.data.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
//I miss you jojo
@Table(name = "accounts")
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false, length = 20)
    private String accountNumber;

    @Column(unique = true, length = 26)
    private String iban;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(name = "account_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(name = "open_date")
    private LocalDateTime openDate;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public enum AccountType { SAVINGS, CURRENT, DEPOSIT }
    public enum AccountStatus { ACTIVE, BLOCKED, CLOSED }

    public Account() {
        this.openDate = LocalDateTime.now();
        this.status = AccountStatus.ACTIVE;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }

    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }

    public LocalDateTime getOpenDate() { return openDate; }
    public void setOpenDate(LocalDateTime openDate) { this.openDate = openDate; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}