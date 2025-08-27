package com.base.encode.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("consignment")
@RequiredArgsConstructor
public class ConsignmentController {
    private final JdbcTemplate jdbcTemplate;

    @PostMapping("/create")
    public ResponseEntity<?> createNewConsignment(@RequestBody ConsignmentRequest request) {
        String sql = "{call New_Consignment_Transaction(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try {
            jdbcTemplate.update(sql,
                    request.getPledgeId(),
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

            return ResponseEntity.ok("Create new Consignment successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Insert failed");
        }
    }

    /*
     * @PostMapping("create")
     * public ResponseEntity<?> createConsignment(@RequestBody AddConsignmentDTO
     * addconsignmentform) {
     * String sql =
     * "INSERT INTO Pledged_Gold (pledge_id, customer_id, weight_gram, gold_type, ref_price, loan_percent, loan_amount, interest_rate, start_date, end_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
     * ;
     * 
     * int result = jdbcTemplate.update(
     * sql,
     * addconsignmentform.getPledgeId(),
     * addconsignmentform.getCustomerId(),
     * addconsignmentform.getWeight(),
     * addconsignmentform.getGoldType(),
     * addconsignmentform.getRefPrice(),
     * addconsignmentform.getLoanPercent(),
     * addconsignmentform.getLoanAmount(),
     * addconsignmentform.getInterestRate(),
     * addconsignmentform.getStartDate(),
     * addconsignmentform.getEndDate());
     * 
     * return result > 0
     * ? ResponseEntity.ok("Create new Consignment successfully")
     * : ResponseEntity.status(500).body("Insert failed");
     * }
     */

    @GetMapping("status/{id}")
    public ResponseEntity<?> getConsignmentHistoryById(@PathVariable int id) {
        String sql = "SELECT * FROM View_Consignment_History WHERE customer_id = ? ORDER BY transaction_date DESC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql, id));
    }

    @GetMapping("status/all")
    public ResponseEntity<?> getConsignmentHistoryAll() {
        String sql = "SELECT * FROM View_Consignment_History ORDER BY transaction_date DESC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
    }

    @PostMapping("/update/status")
    public ResponseEntity<Map<String, Object>> approveConsignment(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer transactionId = (Integer) request.get("transactionId");
            Integer pledgeId = (Integer) request.get("pledgeId");
            Integer customerId = (Integer) request.get("customerId");
            Integer goldType = (Integer) request.get("goldType");
            Double weight = ((Number) request.get("weight")).doubleValue();
            String method = (String) request.get("method");

            String sql = "EXEC Approve_Consignment_Transaction ?, ?, ?, ?, ?, ?";
            jdbcTemplate.update(sql, transactionId, pledgeId, customerId, goldType, weight, method);

            response.put("status", "success");
            response.put("message", "Consignment transaction processed successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

    }

    /*
     * @PostMapping("/update-status")
     * public ResponseEntity<String> updateStatus(
     * 
     * @RequestParam("pledgeId") int pledgeId,
     * 
     * @RequestParam("status") String status) {
     * 
     * // Only allow 'active' or 'reject'
     * if (!status.equalsIgnoreCase("active") && !status.equalsIgnoreCase("reject"))
     * {
     * return ResponseEntity.badRequest().
     * body("Invalid status. Only 'active' or 'reject' allowed.");
     * }
     * 
     * String sql = "UPDATE Pledged_Gold SET status = ? WHERE pledge_id = ?";
     * int rowsAffected = jdbcTemplate.update(sql, status.toLowerCase(), pledgeId);
     * 
     * if (rowsAffected > 0) {
     * return ResponseEntity.ok("Status updated to " + status + " for pledge ID " +
     * pledgeId);
     * } else {
     * return ResponseEntity.badRequest().body("No record found for pledge ID: " +
     * pledgeId);
     * }
     * }
     */

    /*
     * @PostMapping("/transaction")
     * public String createTransaction(@RequestBody AddTransactionConsignDTO
     * transaction) {
     * try {
     * 
     * PledgeGoldDTO pledgeGoldDTO = transaction.getData(); // <-- here
     * String sql2 = "INSERT INTO Pledged_Gold " +
     * "(pledge_id, customer_id, weight, gold_type, ref_price, loan_percent, loan_amount, interest_rate, start_date, end_date) "
     * +
     * "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
     * 
     * jdbcTemplate.update(
     * sql2,
     * pledgeGoldDTO.getPledgeId(),
     * pledgeGoldDTO.getCustomerId(),
     * pledgeGoldDTO.getWeight(),
     * pledgeGoldDTO.getGoldType(),
     * pledgeGoldDTO.getRefPrice(),
     * pledgeGoldDTO.getLoanPercent(),
     * pledgeGoldDTO.getLoanAmount(),
     * pledgeGoldDTO.getInterestRate(),
     * pledgeGoldDTO.getStartDate(),
     * pledgeGoldDTO.getEndDate());
     * 
     * // Insert into Transaction_Logs
     * String sql1 =
     * "INSERT INTO Transaction_Logs (pledge_id, transaction_type, amount) VALUES (?, ?, ?)"
     * ;
     * jdbcTemplate.update(sql1,
     * transaction.getPledge_id(),
     * transaction.getTransaction_type(),
     * transaction.getAmount());
     * 
     * return "Transaction saved successfully!";
     * } catch (Exception e) {
     * e.printStackTrace();
     * return "Error: " + e.getMessage();
     * }
     * }
     */
}
