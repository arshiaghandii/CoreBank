package ir.tejaratBank.core.CoreBank.controller;

import ir.tejaratBank.core.CoreBank.data.model.Customer;
import ir.tejaratBank.core.CoreBank.data.repository.CustomerRepository;
import ir.tejaratBank.core.CoreBank.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ReportService reportService;
    private final CustomerRepository customerRepository;

    public AdminController(ReportService reportService, CustomerRepository customerRepository) {
        this.reportService = reportService;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getAdminDashboard(@RequestParam Long requesterId) {
        Customer requester = customerRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Customer not found!"));
        if (!"ADMIN".equalsIgnoreCase(requester.getRole())) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Access Denied. You are not an ADMIN."));
        }
        return ResponseEntity.ok(reportService.getGeneralReport());
    }
    @PostMapping("/promote")
    public ResponseEntity<?> promoteToAdmin(@RequestParam Long customerId) {
        Customer customer = customerRepository.findById(customerId).get();
        customer.setRole("ADMIN");
        customerRepository.save(customer);
        return ResponseEntity.ok("User " + customerId + " is now ADMIN.");
    }
}