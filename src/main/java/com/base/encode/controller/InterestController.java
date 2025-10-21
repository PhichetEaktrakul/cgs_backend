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
@RequestMapping("interest")
@RequiredArgsConstructor
public class InterestController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/payable/{id}")
    public ResponseEntity<?> getPayableInterestByID(@PathVariable String id) {
        String sql = "SELECT * FROM View_Payable_Interest WHERE customer_id = ? AND end_date >= CAST(GETDATE() AS DATE) ORDER BY due_date ASC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql, id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> newInterestTransaction(@RequestBody Map<String, Object> request) {
        String sql = "EXEC Pay_Interest_Transaction ?, ?, ?, ?";

        try {
            String interestId = (String) request.get("interestId");
            String pledgeId = (String) request.get("pledgeId");
            Double payInterest = ((Number) request.get("payInterest")).doubleValue();
            Double payLoan = ((Number) request.get("payLoan")).doubleValue();

            jdbcTemplate.update(sql, interestId, pledgeId, payInterest, payLoan);

            return ResponseEntity.ok("New Interest transaction created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<?> getInterestHistoryByID(@PathVariable String id) {
        String sql = "SELECT * FROM View_Interest_History WHERE customer_id = ? ORDER BY transaction_date DESC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql, id));
    }

    @GetMapping("/history/all")
    public ResponseEntity<?> getInterestHistoryAll() {
        String sql = "SELECT * FROM View_Interest_History ORDER BY transaction_date DESC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
    }

    @PostMapping("/approve/status")
    public ResponseEntity<?> approveInterestTransaction(@RequestBody Map<String, Object> request) {
        String sql = "EXEC Approve_Interest_Transaction ?, ?, ?, ?, ?, ?, ?, ?, ?";

        try {
            String interestId = (String) request.get("interestId");
            String transactionId = (String) request.get("transactionId");
            String pledgeId = (String) request.get("pledgeId");
            String dueDate = (String) request.get("dueDate"); // Expecting ISO date string from frontend
            String endDate = (String) request.get("endDate"); // Expecting ISO date string from frontend
            Double interestAmount = ((Number) request.get("interestAmount")).doubleValue();
            Double loanAmount = ((Number) request.get("loanAmount")).doubleValue();
            Double intRate = ((Number) request.get("intRate")).doubleValue();
            String method = (String) request.get("method");

            jdbcTemplate.update(sql, interestId, transactionId, pledgeId, dueDate, endDate, interestAmount, loanAmount,
                    intRate, method);

            return ResponseEntity.ok("Update successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

}
