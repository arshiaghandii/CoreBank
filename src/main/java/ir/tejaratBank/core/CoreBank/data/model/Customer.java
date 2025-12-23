package ir.tejaratBank.core.CoreBank.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "نام نمی‌تواند خالی باشد")
    @Size(min = 2, max = 50, message = "نام باید بین ۲ تا ۵۰ کاراکتر باشد")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "نام خانوادگی نمی‌تواند خالی باشد")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotBlank(message = "کد ملی الزامی است")
    @Pattern(regexp = "^\\d{10}$", message = "کد ملی باید دقیقاً ۱۰ رقم باشد")
    @Column(name = "national_id", unique = true, nullable = false, length = 10)
    private String nationalId;

    @Pattern(regexp = "^09\\d{9}$", message = "شماره موبایل باید با ۰۹ شروع شود و ۱۱ رقم باشد")
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Size(max = 255, message = "آدرس خیلی طولانی است")
    @Column(name = "address")
    private String address;

    @NotNull(message = "تاریخ تولد الزامی است")
    @Past(message = "تاریخ تولد باید مربوط به گذشته باشد")
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(nullable = false)
    private String role;


    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts;

    public Customer() {
        this.role = "USER";
    }

    public Customer(String firstName, String lastName, String nationalId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationalId = nationalId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }


}
