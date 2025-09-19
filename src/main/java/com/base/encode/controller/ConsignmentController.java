package com.base.encode.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

            return ResponseEntity.ok("New consignment transaction created successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<?> getConsignmentHistoryByID(@PathVariable int id) {
        String sql = "SELECT * FROM View_Consignment_History WHERE customer_id = ? ORDER BY transaction_date DESC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql, id));
    }

    @GetMapping("/history/all")
    public ResponseEntity<?> getConsignmentHistoryAll() {
        String sql = "SELECT * FROM View_Consignment_History ORDER BY transaction_date DESC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
    }

    @GetMapping("/monitor/{id}")
    public ResponseEntity<?> getMonitoringTicketsByID(@PathVariable int id) {
        String sql = "SELECT * FROM View_Consignment_Ticket WHERE status = 'active' AND end_date >= CAST(GETDATE() AS DATE) AND customer_id = ? ORDER BY transaction_date DESC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql, id));
    }

    @GetMapping("/monitor/all")
    public ResponseEntity<?> getMonitoringTicketsAll() {
        String sql = "SELECT * FROM View_Consignment_Ticket WHERE status = 'active' AND end_date >= CAST(GETDATE() AS DATE) ORDER BY transaction_date DESC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
    }

    @PostMapping("/approve/status")
    public ResponseEntity<?> approveConsignmentTransaction(@RequestBody Map<String, Object> request) {
        String sql = "EXEC Approve_Consignment_Transaction ?, ?, ?, ?, ?, ?, ?";

        try {
            Integer transactionId = (Integer) request.get("transactionId");
            Integer pledgeId = (Integer) request.get("pledgeId");
            Integer customerId = (Integer) request.get("customerId");
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

    /* TEMP Controller */
    @PostMapping("/order-switch")
    public ResponseEntity<?> switchOrderStatus(@RequestBody Map<String, Object> payload) {
        String sql = "UPDATE Pledged_Gold SET pledge_order = ? WHERE pledge_id = ?";

        List<Integer> pledgeIds = (List<Integer>) payload.get("pledgeIds");
        String status = (String) payload.get("status");

        for (Integer id : pledgeIds) {
            jdbcTemplate.update(sql, status, id);
        }

        return ResponseEntity.ok("Status updated");
    }

    @PutMapping("/open-market")
    public ResponseEntity<?> toggleMarket() {
        String sqlUpdate = "UPDATE Open_Market " +
                "SET market = CASE WHEN market = 1 THEN 0 ELSE 1 END " +
                "WHERE id = 1";
        int updated = jdbcTemplate.update(sqlUpdate);

        if (updated == 0) {
            return ResponseEntity.status(404).body("Open_Market not found with id = 1");
        }
        return ResponseEntity.ok("Success! Open_Market toggled.");
    }

}
