package com.base.encode.controller;

import com.base.encode.model.DTO.CustomerOuter;
import com.base.encode.model.DTO.InitialConfigRequest;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("customer")
@RequiredArgsConstructor
public class CustomerController {

    private final JdbcTemplate jdbcTemplate;

    // ================= Get data of customer-outer By ID =================
    @GetMapping("/outer/{id}")
    public ResponseEntity<?> getCustomerOuterByID(@PathVariable String id) {
        String sql = "SELECT * FROM Customers_Outer WHERE customer_id = ?";

        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, id);
            return ResponseEntity.ok(result);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Customer not found"));
        }
    }

    // ================= Get gold data of customer-outer By ID =================
    @GetMapping("/outer/{id}/gold")
    public ResponseEntity<?> getCustomerOuterGoldByID(@PathVariable String id) {
        String sql = "SELECT balance96, balance99 FROM Customers_Outer WHERE customer_id = ?";

        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, id);
            return ResponseEntity.ok(result);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Customer not found"));
        }
    }

    // ================= Add or Subtract customer-outer gold balance
    // =================
    @PostMapping("/outer/goldupdate")
    public ResponseEntity<?> updateCustomerOuterBalance(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String customerId = (String) request.get("customerId");
            Integer goldType = (Integer) request.get("goldType");
            Double weight = ((Number) request.get("weight")).doubleValue();
            String method = (String) request.get("method");

            String sql = "EXEC AddSubtract_Customer_Balance ?, ?, ?, ?";
            jdbcTemplate.update(sql, customerId, goldType, weight, method);

            response.put("status", "success");
            response.put("message", "Balance updated successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ================= Get initial data[Loan% and Interest rate and Number of
    // Installment] of Customer =================
    @GetMapping("/initial/{id}")
    public ResponseEntity<?> getCustomerInitialByID(@PathVariable String id) {
        String sql = "SELECT loan_percent, interest_rate, num_pay FROM Customers WHERE customer_id = ?";

        try {
            Map<String, Object> Initial = jdbcTemplate.queryForMap(sql, id);
            return ResponseEntity.ok(Initial);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Customer not found"));
        }
    }

    // ================= Update Initial data[Loan% and Interest rate and Number of
    // Installment] of Customer =================
    @PutMapping("/initial")
    public ResponseEntity<?> updateCustomerInitial(@RequestBody InitialConfigRequest request) {
        String sql = "UPDATE Customers SET loan_percent = ?, interest_rate = ? WHERE customer_id = ?";

        int rows = jdbcTemplate.update(sql, request.getLoanPercent(), request.getInterestRate(),
                request.getCustomerId());

        if (rows > 0) {
            return ResponseEntity.ok().body("Initial updated successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Customer not found or update failed.");
        }
    }

}
