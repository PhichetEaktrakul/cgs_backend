package com.base.encode.controller;

import com.base.encode.model.DTO.Customer;
import com.base.encode.model.DTO.CustomerMetaEdit;

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
    public ResponseEntity<?> getCustomerOuterByID(@PathVariable int id) {
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
    public ResponseEntity<?> getCustomerOuterGoldByID(@PathVariable int id) {
        String sql = "SELECT balance96, balance99 FROM Customers_Outer WHERE customer_id = ?";

        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, id);
            return ResponseEntity.ok(result);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Customer not found"));
        }
    }

    // ================= Add or Subtract customer-outer gold balance =================
    @PostMapping("/outer/goldupdate")
    public ResponseEntity<?> updateCustomerOuterBalance(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer customerId = (Integer) request.get("customerId");
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

    // ================= Check if customer already accept TOS =================
    @GetMapping("/tos/{id}")
    public ResponseEntity<Boolean> checkCustomerTOS(@PathVariable int id) {
        String sql = "SELECT COUNT(*) FROM Customers WHERE customer_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);

        boolean exists = count != null && count > 0;
        return ResponseEntity.ok(exists);
    }

    // ================= Add customer after customer accept TOS =================
    @PostMapping("/tos/add")
    public ResponseEntity<?> addCustomerTOS(@RequestBody Customer custform) {
        String sql = "INSERT INTO Customers (customer_id, first_name, last_name, phone_number, id_card_number, address) VALUES (?, ?, ?, ?, ?, ?)";

        int result = jdbcTemplate.update(
                sql,
                custform.getCustId(),
                custform.getFirstname(),
                custform.getLastname(),
                custform.getPhonenumber(),
                custform.getIdcard(),
                custform.getAddress());

        return result > 0
                ? ResponseEntity.ok("Customer inserted successfully")
                : ResponseEntity.status(500).body("Insert failed");
    }

    // ================= Get meta data[loan% and Int rate] of Customer =================
    @GetMapping("/meta/{id}")
    public ResponseEntity<?> getCustomerMetaByID(@PathVariable int id) {
        String sql = "SELECT loan_percent, interest_rate FROM Customers WHERE customer_id = ?";

        try {
            Map<String, Object> meta = jdbcTemplate.queryForMap(sql, id);
            return ResponseEntity.ok(meta);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Customer not found"));
        }
    }

    // ================= Update meta data[loan% and Int rate] of Customer =================
    @PutMapping("/meta")
    public ResponseEntity<?> updateCustomerMeta(@RequestBody CustomerMetaEdit request) {
        String sql = "UPDATE Customers SET loan_percent = ?, interest_rate = ? WHERE customer_id = ?";
        int rows = jdbcTemplate.update(sql, request.getLoanPercent(), request.getInterestRate(),
                request.getCustomerId());

        if (rows > 0) {
            return ResponseEntity.ok().body("Rates updated successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Customer not found or update failed.");
        }
    }

    // ================= Get All Customer or Search Customer [Admin Panel] =================
    @GetMapping({ "/admin", "/admin/{id}" })
    public ResponseEntity<?> getCustomers(@PathVariable(required = false) Integer id) {
        if (id == null) {
            String sql = "SELECT * FROM Customers";
            return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
        } else {
            String sql = "SELECT * FROM Customers WHERE customer_id = ?";
            return ResponseEntity.ok(jdbcTemplate.queryForList(sql, id));
        }
    }

}
