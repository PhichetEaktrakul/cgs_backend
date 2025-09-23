package com.base.encode.controller;

import com.base.encode.model.DTO.GoldAssnResponse;
import com.base.encode.service.GoldPriceAssnService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("gold-assn")
@RequiredArgsConstructor
public class GoldPriceAssnController {

    private final JdbcTemplate jdbcTemplate;
    private final GoldPriceAssnService goldscraperService;

    @GetMapping("/scrapping")
    public ResponseEntity<Map<String, String>> scrapGoldPrice() {
        try {
            Map<String, String> prices = goldscraperService.getGoldPrices();
            return ResponseEntity.ok(prices);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<GoldAssnResponse> getGoldPriceAssnLatest() {
        String sql = "SELECT TOP 1 * FROM GoldPrices_Assn ORDER BY id DESC";

        try {
            GoldAssnResponse latest = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new GoldAssnResponse(
                    rs.getLong("id"),
                    rs.getString("sell_price"),
                    rs.getString("buy_price"),
                    rs.getString("updated_time"),
                    rs.getString("created_at")));
            return ResponseEntity.ok(latest);
        } catch (Exception e) {
            return ResponseEntity.ok(null);
        }
    }

    // ================= Get last updated_time from DB =================
    private String getUpdateLatest() {
        String sql = "SELECT TOP 1 updated_time FROM GoldPrices_Assn ORDER BY id DESC";

        try {
            return jdbcTemplate.queryForObject(sql, String.class);
        } catch (Exception e) {
            return null; // if no data yet
        }
    }

    // ================= Insert a record into DB =================
    private void saveGoldPriceAssn(String sell, String buy, String updatedTime) {
        String sql = "INSERT INTO GoldPrices_Assn (sell_price, buy_price, updated_time) VALUES (?,?,?)";

        jdbcTemplate.update(sql, sell, buy, updatedTime);
    }

   // ================= Check every 10 minutes for a new price =================
    @Scheduled(fixedRate = 600_000, initialDelay = 0)
    public void scheduledGoldPriceAssn() {
        System.out.println("Scheduler running...");
        try {
            var prices = goldscraperService.getGoldPrices();
            String sell = prices.get("Sell Price");
            String buy = prices.get("Buy Price");
            String updatedTime = prices.get("Updated Time");

            String lastUpdatedTime = getUpdateLatest();

            if (lastUpdatedTime == null || !updatedTime.equals(lastUpdatedTime)) {
                saveGoldPriceAssn(sell, buy, updatedTime);
                System.out.println("New gold price saved");
            } else {
                System.out.println("No new update");
            }

        } catch (Exception e) {
            System.err.println("Error fetching/saving gold prices: " + e.getMessage());
        }
    }

}
