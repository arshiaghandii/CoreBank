package ir.tejaratBank.core.CoreBank.controller;

import ir.tejaratBank.core.CoreBank.data.model.Customer;
import ir.tejaratBank.core.CoreBank.data.repository.CustomerRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "register-customer";
    }

    @PostMapping("/register")
    public String registerCustomer(@Valid @ModelAttribute("customer") Customer customer,
                                   BindingResult bindingResult,
                                   Model model) {

        if (customerRepository.existsByNationalId(customer.getNationalId())) {
            bindingResult.rejectValue("nationalId", "error.customer", "این کد ملی قبلاً در سیستم ثبت شده است.");
        }

        if (bindingResult.hasErrors()) {
            return "register-customer";
        }

        customerRepository.save(customer);
        return "redirect:/customers/register?success";
    }
}