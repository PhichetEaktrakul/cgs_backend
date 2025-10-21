package com.base.encode.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.base.encode.model.DTO.CustomerOuter;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("tos")
@RequiredArgsConstructor
public class TermsServiceController {

    private final JdbcTemplate jdbcTemplate;

    // ================= Check if customer already accept TOS =================
    @GetMapping("/check/{id}")
    public ResponseEntity<Boolean> checkCustomerTOS(@PathVariable String id) {
        String sql = "SELECT COUNT(*) FROM Customers WHERE customer_id = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        boolean exists = count != null && count > 0;
        return ResponseEntity.ok(exists);
    }

    // ================= Add customer after customer accept TOS =================
    @PostMapping("/add")
    public ResponseEntity<?> addCustomerTOS(@RequestBody CustomerOuter custOuter) {
        try {
            var conf = jdbcTemplate.queryForMap(
                    "SELECT TOP 1 loan_percent, interest_rate, num_pay FROM Init_Config ORDER BY id DESC");

            int res = jdbcTemplate.update(
                    """
                                INSERT INTO Customers
                                (customer_id, first_name, last_name, phone_number, id_card_number, address, loan_percent, interest_rate, num_pay)
                                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                            """,
                    custOuter.getCustomerId(), custOuter.getFirstname(), custOuter.getLastname(),
                    custOuter.getPhonenumber(), custOuter.getIdcard(), custOuter.getAddress(),
                    conf.get("loan_percent"), conf.get("interest_rate"), conf.get("num_pay"));

            return ResponseEntity.ok(res > 0 ? "Customer inserted" : "Insert failed");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

}
