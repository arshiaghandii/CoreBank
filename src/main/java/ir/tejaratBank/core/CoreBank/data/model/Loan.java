package ir.tejaratBank.core.CoreBank.data.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_number", unique = true, nullable = false)
    private String loanNumber;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "remaining_amount", nullable = false)
    private BigDecimal remainingAmount;

    @Column(name = "total_installments")
    private Integer totalInstallments;

    @Column(name = "paid_installments")
    private Integer paidInstallments;

    @Column(name = "interest_rate")
    private Double interestRate;

    @Column(name = "start_date")
    private LocalDate startDate;

    // ارتباط با مشتری (وام‌گیرنده)
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // --- سازنده ---
    public Loan() {}

    // --- گتر و سترها ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLoanNumber() { return loanNumber; }
    public void setLoanNumber(String loanNumber) { this.loanNumber = loanNumber; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }

    public Integer getTotalInstallments() { return totalInstallments; }
    public void setTotalInstallments(Integer totalInstallments) { this.totalInstallments = totalInstallments; }

    public Integer getPaidInstallments() { return paidInstallments; }
    public void setPaidInstallments(Integer paidInstallments) { this.paidInstallments = paidInstallments; }

    public Double getInterestRate() { return interestRate; }
    public void setInterestRate(Double interestRate) { this.interestRate = interestRate; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }



}