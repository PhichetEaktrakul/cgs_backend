package com.base.encode.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.base.encode.model.ConsignmentRequest;
import com.base.encode.model.InterestRequest;
import com.base.encode.model.PayableInterestDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("interest")
@RequiredArgsConstructor
public class InterestController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/payable/{id}")
    public ResponseEntity<?> getPayableInterestById(@PathVariable int id) {
        String sql = "SELECT * FROM View_Payable_Interest WHERE customer_id = ? ORDER BY due_date ASC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql, id));

    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createNewInterestTransaction(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer interestId = (Integer) request.get("interestId");
            Integer pledgeId = (Integer) request.get("pledgeId");
            Double payInterest = ((Number) request.get("payInterest")).doubleValue();
            Double payLoan = ((Number) request.get("payLoan")).doubleValue();

            // Call stored procedure
            String sql = "EXEC Pay_Interest_Transaction ?, ?, ?, ?";
            jdbcTemplate.update(sql, interestId, pledgeId, payInterest, payLoan);

            response.put("status", "success");
            response.put("message", "New interest transaction created successfully.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("status/{id}")
    public ResponseEntity<List<Map<String, Object>>> getInterestHistory(@PathVariable int id) {
        String sql = "SELECT * FROM View_Interest_History WHERE customer_id = ? ORDER BY transaction_date DESC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql, id));
    }

    @GetMapping("status/all")
    public ResponseEntity<?> getInterestHistoryAll() {
        String sql = "SELECT * FROM View_Interest_History ORDER BY transaction_date DESC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
    }

    @PostMapping("/update/status")
    public ResponseEntity<?> approveInterestTransaction(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer interestId = (Integer) request.get("interestId");
            Integer transactionId = (Integer) request.get("transactionId");
            Integer pledgeId = (Integer) request.get("pledgeId");
            String dueDate = (String) request.get("dueDate"); // Expecting ISO date string from frontend
            Double loanAmount = ((Number) request.get("loanAmount")).doubleValue();
            Double intRate = ((Number) request.get("intRate")).doubleValue();
            String method = (String) request.get("method");

            String sql = "EXEC Approve_Interest_Transaction ?, ?, ?, ?, ?, ?, ?";
            jdbcTemplate.update(sql, interestId, transactionId, pledgeId, dueDate, loanAmount, intRate, method);

            response.put("status", "success");
            response.put("message", "Interest transaction processed successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
