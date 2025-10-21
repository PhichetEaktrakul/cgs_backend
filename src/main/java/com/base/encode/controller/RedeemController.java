package com.base.encode.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("redeem")
@RequiredArgsConstructor
public class RedeemController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/list/{id}")
    public ResponseEntity<?> getRedeemableConsignmentByID(@PathVariable String id) {
        String sql = "SELECT * FROM View_Redeemable_Consignment WHERE customer_id = ? ORDER BY end_date ASC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql, id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> newRedeemTransaction(@RequestBody Map<String, Object> request) {
        String sql = "EXEC New_Redeem_Transaction ?, ?, ?, ?";

        try {
            String transactionId = (String) request.get("transactionId");
            String pledgeId = (String) request.get("pledgeId");
            Double principalPay = ((Number) request.get("principalPay")).doubleValue();
            Double interestPay = ((Number) request.get("interestPay")).doubleValue();

            jdbcTemplate.update(sql, transactionId, pledgeId, principalPay, interestPay);

            return ResponseEntity.ok("New Redeem transaction created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<?> getRedeemHistoryByID(@PathVariable String id) {
        String sql = "SELECT * FROM View_Redeem_History WHERE customer_id = ? ORDER BY transaction_date DESC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql, id));
    }

    @GetMapping("/history/all")
    public ResponseEntity<?> getRedeemHistoryAll() {
        String sql = "SELECT * FROM View_Redeem_History ORDER BY transaction_date DESC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
    }

    @PostMapping("/approve/status")
    public ResponseEntity<?> approveRedeemTransaction(@RequestBody Map<String, Object> request) {
        String sql = "EXEC Approve_Redeem_Transaction ?, ?, ?, ?, ?, ?, ?, ?";

        try {
            String transId = (String) request.get("transId");
            String pledgeId = (String) request.get("pledgeId");
            Integer goldType = (Integer) request.get("goldType");
            Double intPaid = ((Number) request.get("intPaid")).doubleValue();
            Double prinPaid = ((Number) request.get("prinPaid")).doubleValue();
            Double weight = ((Number) request.get("weight")).doubleValue();
            String custId = (String) request.get("custId");
            String method = (String) request.get("method");

            jdbcTemplate.update(sql, transId, pledgeId, goldType, intPaid, prinPaid, weight, custId, method);

            return ResponseEntity.ok("Update successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

}
