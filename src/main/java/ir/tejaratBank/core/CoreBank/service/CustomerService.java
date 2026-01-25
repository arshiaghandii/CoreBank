package ir.tejaratBank.core.CoreBank.service;
import ir.tejaratBank.core.CoreBank.data.model.Customer;
import ir.tejaratBank.core.CoreBank.data.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public void registerCustomer(Customer customer) {
        customerRepository.save(customer);
    }
    public boolean isNationalIdTaken(String nationalId) {
        return customerRepository.existsByNationalId(nationalId);
    }
}