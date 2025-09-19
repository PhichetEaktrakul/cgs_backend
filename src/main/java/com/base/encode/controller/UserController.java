package com.base.encode.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    public ResponseEntity<?> getUsersAll() {
        String sql = "SELECT * FROM Users WHERE is_delete = 0 AND role <> 'manager' ORDER BY created_at DESC";

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserByID(@PathVariable int id) {
        String sql = "UPDATE Users SET is_active = 0, is_delete = 1 WHERE user_id = ?";

        int rows = jdbcTemplate.update(sql, id);

        if (rows > 0) {
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
