package com.base.encode.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class GoldPriceAssnService {
    private static final String URL = "https://www.goldtraders.or.th/";

    public Map<String, String> getGoldPrices() throws IOException {
        Document doc = Jsoup.connect(URL)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .timeout(10_000)
                .get();

        Map<String, String> prices = new HashMap<>();

        String sellPrice = doc.select("#DetailPlace_uc_goldprices1_lblBLSell").text();  // Extract sell price
        String buyPrice = doc.select("#DetailPlace_uc_goldprices1_lblBLBuy").text();    // Extract buy price
        String updateTime = doc.select("#DetailPlace_uc_goldprices1_lblAsTime").text(); // Extract time

        prices.put("Sell Price", sellPrice);
        prices.put("Buy Price", buyPrice);
        prices.put("Updated Time", updateTime);

        return prices;
    }
}
