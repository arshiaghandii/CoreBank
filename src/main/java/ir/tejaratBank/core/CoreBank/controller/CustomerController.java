package ir.tejaratBank.core.CoreBank.controller;

import ir.tejaratBank.core.CoreBank.data.model.Customer;
import ir.tejaratBank.core.CoreBank.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;


    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "register-customer";
    }

    @PostMapping("/register")
    public String registerCustomer(@Valid @ModelAttribute("customer") Customer customer, BindingResult bindingResult, Model model) {

        // لاجیک چک کردن تکراری بودن رو از طریق سرویس صدا بزن
        if (customerService.isNationalIdTaken(customer.getNationalId())) {
            bindingResult.rejectValue("nationalId", "error.customer", "این کد ملی قبلاً در سیستم ثبت شده است.");
        }

        if (bindingResult.hasErrors()) {
            return "register-customer";
        }

        // کار اصلی رو بسپار به سرویس
        customerService.registerCustomer(customer);

        return "redirect:/customers/register?success";
    }


}