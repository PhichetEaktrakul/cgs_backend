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

import com.base.encode.model.DTO.ConsignmentRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("consignment")
@RequiredArgsConstructor
public class ConsignmentController {

    private final JdbcTemplate jdbcTemplate;

    @PostMapping("/create")
    public ResponseEntity<?> newConsignmentTransaction(@RequestBody ConsignmentRequest request) {
        String sql = "{call New_Consignment_Transaction(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql,
                    request.getCustomerId(),
                    request.getWeight(),
                    request.getGoldType(),
                    request.getRefPrice(),
                    request.getLoanPercent(),
                    request.getLoanAmount(),
                    request.getInterestRate(),
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getTransactionType());

            String pledgeId = (String) result.get("pledge_id");
            return ResponseEntity.ok(pledgeId);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<?> getConsignmentHistoryByID(@PathVariable String id) {
        String sql = "SELECT * FROM View_Consignment_History WHERE customer_id = ? ORDER BY transaction_date DESC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql, id));
    }

    @GetMapping("/history/all")
    public ResponseEntity<?> getConsignmentHistoryAll() {
        String sql = "SELECT * FROM View_Consignment_History ORDER BY transaction_date DESC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
    }

    @PostMapping("/approve/status")
    public ResponseEntity<?> approveConsignmentTransaction(@RequestBody Map<String, Object> request) {
        String sql = "EXEC Approve_Consignment_Transaction ?, ?, ?, ?, ?, ?, ?";

        try {
            String transactionId = (String) request.get("transactionId");
            String pledgeId = (String) request.get("pledgeId");
            String customerId = (String) request.get("customerId");
            Integer goldType = (Integer) request.get("goldType");
            Double weight = ((Number) request.get("weight")).doubleValue();
            Double loanAmount = ((Number) request.get("loanAmount")).doubleValue();
            String method = (String) request.get("method");

            jdbcTemplate.update(sql, transactionId, pledgeId, customerId, goldType, weight, loanAmount, method);

            return ResponseEntity.ok("Update successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }

    }

}
