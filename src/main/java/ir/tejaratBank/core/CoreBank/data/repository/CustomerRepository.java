package ir.tejaratBank.core.CoreBank.data.repository;

import ir.tejaratBank.core.CoreBank.data.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Optional<Customer> findByFirstName(String first_name);
    boolean existsByNationalId(String nationalId);

    Optional<Customer> findByNationalId(String username);
}
















