package com.base.encode.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final JdbcTemplate jdbcTemplate;

    // ===========================================================================================
    @GetMapping("/monitor/{id}")
    public ResponseEntity<?> getMonitoringTicketsByID(@PathVariable String id) {
        String sql = "SELECT * FROM View_Consignment_Ticket WHERE status = 'active' AND end_date >= CAST(GETDATE() AS DATE) AND customer_id = ? ORDER BY transaction_date DESC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql, id));
    }

    @GetMapping("/monitor/all")
    public ResponseEntity<?> getMonitoringTicketsAll() {
        String sql = "SELECT * FROM View_Consignment_Ticket WHERE status = 'active' AND end_date >= CAST(GETDATE() AS DATE) ORDER BY transaction_date DESC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
    }
    // ===========================================================================================

    // ===========================================================================================
    /* TEMP Controller */
    @PostMapping("/order-switch")
    public ResponseEntity<?> switchOrderStatus(@RequestBody Map<String, Object> payload) {
        String sql = "UPDATE Pledged_Gold SET pledge_order = ? WHERE pledge_id = ?";

        List<String> pledgeIds = (List<String>) payload.get("pledgeIds");
        String status = (String) payload.get("status");

        for (String id : pledgeIds) {
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
    // ===========================================================================================

    // ===========================================================================================
    @GetMapping("/user")
    public ResponseEntity<?> getUsersAll() {
        String sql = "SELECT * FROM Users WHERE is_delete = 0 AND role <> 'manager' ORDER BY created_at DESC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUserByID(@PathVariable int id) {
        String sql = "UPDATE Users SET is_active = 0, is_delete = 1 WHERE user_id = ?";

        int rows = jdbcTemplate.update(sql, id);

        if (rows > 0) {
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // ===========================================================================================

    // ===========================================================================================
    // Get All Customer or Search Customer [Admin Panel]
    @GetMapping({ "/getcustomer", "/getcustomer/{id}" })
    public ResponseEntity<?> getCustomers(@PathVariable(required = false) String id) {
        if (id == null) {
            String sql = "SELECT * FROM Customers";
            return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
        } else {
            String sql = "SELECT * FROM Customers WHERE customer_id = ?";
            return ResponseEntity.ok(jdbcTemplate.queryForList(sql, id));
        }
    }
    // ===========================================================================================



    @GetMapping("/config")
    public List<Map<String, Object>> getAll() {
        String sql = "SELECT id, loan_percent, interest_rate, num_pay FROM Init_Config";
        return jdbcTemplate.queryForList(sql);
    }


    @PutMapping("/config")
    public ResponseEntity<?> updateConfig(@RequestBody Map<String, Object> body) {
        String sql = """
            UPDATE Init_Config
            SET loan_percent = ?, interest_rate = ?, num_pay = ?
            WHERE id = 1
        """;

        Object loanPercent = body.get("loanPercent");
        Object interestRate = body.get("interestRate");
        Object numPay = body.get("numPay");

        int rows = jdbcTemplate.update(sql, loanPercent, interestRate, numPay);

        if (rows > 0) {
            return ResponseEntity.ok(Map.of("message", "Updated successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }






}
