package ir.tejaratBank.core.CoreBank.data.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private String description;

    @Column(name = "tracking_code", length = 20)
    private String trackingCode;

    @Column(name = "source_account_number")
    private String sourceAccountNumber;

    @Column(name = "destination_account_number")
    private String destinationAccountNumber;

    // ارتباط با حساب
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public enum TransactionType { DEPOSIT, WITHDRAW, TRANSFER_IN, TRANSFER_OUT }

    // --- سازنده ---
    public Transaction() {
        this.timestamp = LocalDateTime.now();
    }

    // --- گتر و سترها ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }

    public String getSourceAccountNumber() { return sourceAccountNumber; }
    public void setSourceAccountNumber(String sourceAccountNumber) { this.sourceAccountNumber = sourceAccountNumber; }

    public String getDestinationAccountNumber() { return destinationAccountNumber; }
    public void setDestinationAccountNumber(String destinationAccountNumber) { this.destinationAccountNumber = destinationAccountNumber; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
}