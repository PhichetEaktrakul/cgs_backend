package com.base.encode.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.base.encode.model.DTO.TokenRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("token")
@RequiredArgsConstructor
public class EncodeController {

    @Value("${myapp.api.url}")
    private String apiURL;

    @PostMapping("/encode")
    public ResponseEntity<?> encodeToken(@RequestBody TokenRequest token) throws Exception {
        String customerId = token.getCustomerId();
        String firstname = token.getFirstname();
        String lastname = token.getLastname();
        String phone = token.getPhonenumber();
        String idcard = token.getIdcard();
        String address = token.getAddress();
        String source = token.getSource();

        String rawToken = "customerId=" + customerId + "&firstname=" + firstname + "&lastname=" + lastname + "&phone="
                + phone + "&idcard=" + idcard + "&address=" + address + "&source=" + source;
        String keyString = "5dkoaldjcmsldkwo75dd52s5d6d3v5a7";

        String encryptedToken = encrypt(keyString, rawToken);
        String url = apiURL + "?token=" + URLEncoder.encode(encryptedToken, StandardCharsets.UTF_8);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/decode")
    public ResponseEntity<?> decodeToken(@RequestParam String token) {
        try {
            System.out.println("Received token (raw): " + token);

            String key = "5dkoaldjcmsldkwo75dd52s5d6d3v5a7";

            // Try without URLDecoder
            String decrypted = decrypt(key, token);
            System.out.println("Decrypted: " + decrypted);

            String[] parts = decrypted.split("&");
            Map<String, String> result = new HashMap<>();
            for (String part : parts) {
                String[] kv = part.split("=");
                if (kv.length == 2) {
                    result.put(kv[0], kv[1]);
                }
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace(); // Print the error to debug
            return ResponseEntity.badRequest().body("Invalid token or decryption error.");
        }
    }

    private String encrypt(String key, String plainText) throws Exception {
        // Convert key and IV to bytes
        byte[] iv = "vy1sDUUiXplyTJbB".getBytes(StandardCharsets.UTF_8); // Must be 16 bytes
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8); // Must be 16/24/32 bytes for AES

        // Prepare AES cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // CBC mode + padding
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Return Base64 encoded string (like Convert.ToBase64String in C#)
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String key, String cipherText) throws Exception {
        byte[] iv = "vy1sDUUiXplyTJbB".getBytes(StandardCharsets.UTF_8); // must be 16 bytes
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8); // must be 16/24/32 bytes
        byte[] cipherBytes = Base64.getDecoder().decode(cipherText);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

        byte[] decrypted = cipher.doFinal(cipherBytes);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

}
