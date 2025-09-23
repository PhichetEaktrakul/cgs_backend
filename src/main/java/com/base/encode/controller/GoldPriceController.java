package com.base.encode.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.base.encode.model.DTO.GoldPriceLog;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("gold-prices")
@RequiredArgsConstructor
public class GoldPriceController {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<GoldPriceLog> rowMapper = (rs, rowNum) -> {
        GoldPriceLog dto = new GoldPriceLog();
        dto.setId(rs.getLong("id"));
        dto.setGold99_buy(rs.getDouble("gold99_buy"));
        dto.setGold99_sell(rs.getDouble("gold99_sell"));
        dto.setOld_gold99_buy(rs.getDouble("old_gold99_buy"));
        dto.setOld_gold99_sell(rs.getDouble("old_gold99_sell"));
        dto.setGold96_buy(rs.getDouble("gold96_buy"));
        dto.setGold96_sell(rs.getDouble("gold96_sell"));
        dto.setOld_gold96_buy(rs.getDouble("old_gold96_buy"));
        dto.setOld_gold96_sell(rs.getDouble("old_gold96_sell"));
        dto.setCreated_at(rs.getString("created_at"));
        return dto;
    };

    @GetMapping
    public ResponseEntity<List<GoldPriceLog>> getGoldPriceAll() {
        String sql = "SELECT * FROM GoldPrices ORDER BY created_at DESC";

        return ResponseEntity.ok(jdbcTemplate.query(sql, rowMapper));
    }

    @GetMapping("/latest")
    public ResponseEntity<GoldPriceLog> getGoldPriceLatest() {
        String sql = "SELECT TOP 1 * FROM GoldPrices ORDER BY created_at DESC";

        List<GoldPriceLog> list = jdbcTemplate.query(sql, rowMapper);

        if (list.isEmpty()) {
            return ResponseEntity.ok(new GoldPriceLog());
        }
        return ResponseEntity.ok(list.get(0));
    }

    @PostMapping
    public ResponseEntity<?> saveGoldPrice(@RequestBody GoldPriceLog dto) {
        String sql = """
                    INSERT INTO GoldPrices
                    (gold99_buy, gold99_sell, old_gold99_buy, old_gold99_sell,
                     gold96_buy, gold96_sell, old_gold96_buy, old_gold96_sell)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        int rows = jdbcTemplate.update(sql,
                dto.getGold99_buy(),
                dto.getGold99_sell(),
                dto.getOld_gold99_buy(),
                dto.getOld_gold99_sell(),
                dto.getGold96_buy(),
                dto.getGold96_sell(),
                dto.getOld_gold96_buy(),
                dto.getOld_gold96_sell());

        if (rows > 0) {
            return ResponseEntity.ok("Gold price saved successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to save gold price");
        }
    }

}
