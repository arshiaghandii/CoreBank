package ir.tejaratBank.core.CoreBank.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter@Setter
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

    @Version
    @Column(name = "opt_lock_version")
    private Long version;


    @Column(name = "open_date")
    private LocalDateTime openDate;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public enum AccountType {SAVINGS, CURRENT, DEPOSIT}

    public enum AccountStatus {ACTIVE, BLOCKED, CLOSED}

    public Account() {
        this.openDate = LocalDateTime.now();
        this.status = AccountStatus.ACTIVE;
    }


}