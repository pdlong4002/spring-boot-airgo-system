package com.ramennsama.springboot.paymentservice;

import com.ramennsama.springboot.paymentservice.utils.VnPayUtil;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class VNPayHashTest {

    @Test
    public void testCreationHash() throws Exception {
        String hashSecret = "53QVOMW5H8V6S0Y6TBBESVPW6MPD4OEI";
        String expectedCreationHash = "dea5937be01b934d0093c0aebcf212b1408eb0943be86fae4ae2c55aceeb12bb883c9d5d6f69f70ddc37d8ef41a22a3acd3a8f35924aa838821f385ae48b7df4";

        Map<String, String> creationParams = new HashMap<>();
        creationParams.put("vnp_Amount", "381000000");
        creationParams.put("vnp_Command", "pay");
        creationParams.put("vnp_CreateDate", "20260519215827");
        creationParams.put("vnp_CurrCode", "VND");
        creationParams.put("vnp_IpAddr", "127.0.0.1");
        creationParams.put("vnp_Locale", "vn");
        creationParams.put("vnp_OrderInfo", "Thanh toan don hang AG9991");
        creationParams.put("vnp_OrderType", "other");
        creationParams.put("vnp_ReturnUrl", "http://localhost:8085/api/v1/vnpay/return");
        creationParams.put("vnp_TmnCode", "URHM5JJK");
        creationParams.put("vnp_TxnRef", "AG9991");
        creationParams.put("vnp_Version", "2.1.0");

        System.out.println("=== TESTING CREATION HASH ===");
        checkHash(creationParams, true, hashSecret, expectedCreationHash, "UTF-8 %20");
        checkHash(creationParams, false, hashSecret, expectedCreationHash, "UTF-8 +");
        checkRawHash(creationParams, hashSecret, expectedCreationHash, "Raw");
    }

    private void checkHash(Map<String, String> params, boolean replacePlus, String secret, String expected, String label) throws Exception {
        java.util.List<String> fieldNames = new java.util.ArrayList<>(params.keySet());
        java.util.Collections.sort(fieldNames);
        StringBuilder query = new StringBuilder();
        for (String field : fieldNames) {
            String value = params.get(field);
            if (value != null && !value.isEmpty()) {
                String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
                if (replacePlus) {
                    encodedValue = encodedValue.replace("+", "%20");
                }
                query.append(field).append("=").append(encodedValue).append("&");
            }
        }
        String signData = query.substring(0, query.length() - 1);
        String hash = VnPayUtil.hmacSHA512(secret, signData);
        System.out.println("  " + label + " Hash: " + hash + " -> Match: " + hash.equals(expected));
    }

    private void checkRawHash(Map<String, String> params, String secret, String expected, String label) throws Exception {
        java.util.List<String> fieldNames = new java.util.ArrayList<>(params.keySet());
        java.util.Collections.sort(fieldNames);
        StringBuilder query = new StringBuilder();
        for (String field : fieldNames) {
            String value = params.get(field);
            if (value != null && !value.isEmpty()) {
                query.append(field).append("=").append(value).append("&");
            }
        }
        String signData = query.substring(0, query.length() - 1);
        String hash = VnPayUtil.hmacSHA512(secret, signData);
        System.out.println("  " + label + " Hash: " + hash + " -> Match: " + hash.equals(expected));
    }
}
