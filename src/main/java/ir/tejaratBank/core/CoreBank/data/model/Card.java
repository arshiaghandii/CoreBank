package ir.tejaratBank.core.CoreBank.data.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", unique = true, nullable = false, length = 16)
    private String cardNumber;

    @Column(nullable = false, length = 4)
    private String cvv2;

    @Column(name = "expire_date", nullable = false, length = 5)
    private String expireDate; //

    @Column(nullable = false)
    private String pin;

    @Column(name = "is_active")
    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public Card() {
        this.isActive = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCvv2() { return cvv2; }
    public void setCvv2(String cvv2) { this.cvv2 = cvv2; }

    public String getExpireDate() { return expireDate; }
    public void setExpireDate(String expireDate) { this.expireDate = expireDate; }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
}