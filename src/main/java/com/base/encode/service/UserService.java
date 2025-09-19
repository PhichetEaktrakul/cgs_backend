package com.base.encode.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Service
@Component
@RequiredArgsConstructor
public class UserService {

    private final JdbcTemplate jdbcTemplate;

    public void createUser(String username, String passwordHash, String role) {
        String sql = """
                INSERT INTO Users (username, password_hash, role, is_active, is_delete, created_at)
                VALUES (?, ?, ?, 1, 0, GETDATE())
                """;
                
        jdbcTemplate.update(sql, username, passwordHash, role);
    }

    /**
     * Find user by username (not deleted)
     */
    public Optional<UserRecord> findUserByUsername(String username) {
        String sql = "SELECT username, password_hash, role FROM Users WHERE username = ? AND is_delete = 0";
        try {
            UserRecord user = jdbcTemplate.queryForObject(sql, this::mapRowToUser, username);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private UserRecord mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return new UserRecord(
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("role"));
    }

    /**
     * Simple immutable record for user row
     */
    public record UserRecord(String username, String passwordHash, String role) {
    }
}
