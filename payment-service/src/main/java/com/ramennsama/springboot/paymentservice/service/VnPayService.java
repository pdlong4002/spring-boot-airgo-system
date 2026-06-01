package com.ramennsama.springboot.paymentservice.service;

import com.ramennsama.springboot.paymentservice.enums.PaymentMethod;
import com.ramennsama.springboot.paymentservice.utils.VnPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class VnPayService {

    @Value("${vnpay.version}")
    private String version;

    @Value("${vnpay.command}")
    private String command;

    @Value("${vnpay.currCode}")
    private String currCode;

    @Value("${vnpay.ortherType}")
    private String ortherType;

    @Value("${vnpay.locale}")
    private String locale;

    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.hashSecret}")
    private String hashSecret;

    @Value("${vnpay.payUrl}")
    private String payUrl;

    @Value("${vnpay.returnUrl}")
    private String returnUrl;

    // public String createPaymentUrl(BigDecimal amount, String txnRef, String
    // fullName, String email,
    // PaymentMethod paymentMethod) {

    // Map<String, String> vnpParams = new HashMap<>();

    // vnpParams.put("vnp_Version", version);
    // vnpParams.put("vnp_Command", command);
    // vnpParams.put("vnp_TmnCode", tmnCode);

    // // Hệ thống đang lưu giá trị amount là USD (Ví dụ 150.00 USD).
    // // VNPay chỉ nhận VND. Cần quy đổi tỷ giá (Ví dụ: 1 USD = 25,400 VND)
    // BigDecimal exchangeRate = BigDecimal.valueOf(25400);
    // BigDecimal amountInVND = amount.multiply(exchangeRate); // 150 USD * 25400 =
    // 3,810,000 VND

    // // VNPay yêu cầu số tiền gửi lên phải nhân thêm 100 lần (tức là 381,000,000)
    // long vnpAmount = amountInVND.multiply(BigDecimal.valueOf(100)).longValue();

    // vnpParams.put("vnp_Amount", String.valueOf(vnpAmount));
    // vnpParams.put("vnp_CurrCode", currCode);

    // vnpParams.put("vnp_TxnRef", txnRef);
    // vnpParams.put("vnp_OrderInfo", "Thanh toan ve may bay AirGo - Ma " + txnRef);
    // vnpParams.put("vnp_OrderType", ortherType);
    // vnpParams.put("vnp_Locale", locale);

    // // Đính kèm các thông tin tùy chọn (fullName, email, paymentMethod) vào
    // // returnUrl
    // String customReturnUrl = returnUrl + "?fullName=" +
    // URLEncoder.encode(fullName, StandardCharsets.UTF_8)
    // + "&email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
    // + "&paymentMethod=" + paymentMethod.name();
    // vnpParams.put("vnp_ReturnUrl", customReturnUrl);
    // vnpParams.put("vnp_IpAddr", "127.0.0.1");

    // SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    // formatter.setTimeZone(java.util.TimeZone.getTimeZone("GMT+7")); // Rất quan
    // trọng: Phải ép múi giờ VN
    // vnpParams.put("vnp_CreateDate", formatter.format(new Date()));

    // String query = VnPayUtil.buildQuery(vnpParams);
    // String secureHash = VnPayUtil.hmacSHA512(hashSecret, query);

    // return payUrl + "?" + query + "&vnp_SecureHash=" + secureHash;
    // }

    public boolean verifyReturn(Map<String, String> params) {
    log.info("=== VNPay Callback Signature Verification ===");
    log.info("Incoming Params: {}", params);

    // Sao chép map để tránh làm thay đổi map gốc
    Map<String, String> verifyParams = new HashMap<>(params);
    String vnpSecureHash = verifyParams.remove("vnp_SecureHash");

    // Loại bỏ các custom parameter của chúng ta để tính checksum chính xác theo
    //thuật toán của VNPay
    verifyParams.remove("fullName");
    verifyParams.remove("email");
    verifyParams.remove("paymentMethod");

    String signData = VnPayUtil.buildQuery(verifyParams);

    log.info("--- Sorted Params for Hashing ---");
    List<String> fieldNames = new ArrayList<>(verifyParams.keySet());
    Collections.sort(fieldNames);
    for (String fieldName : fieldNames) {
    log.info(" {} = {}", fieldName, verifyParams.get(fieldName));
    }
    log.info("---------------------------------");

    String checkHash = VnPayUtil.hmacSHA512(hashSecret, signData);

    log.info("Sign Data (Raw Query): {}", signData);
    log.info("Calculated Hash: {}", checkHash);
    log.info("Received SecureHash: {}", vnpSecureHash);
    log.info("HashSecret (Length: {}): {}...{}",
    hashSecret != null ? hashSecret.length() : 0,
    hashSecret != null && hashSecret.length() > 4 ? hashSecret.substring(0, 4) :
    "null",
    hashSecret != null && hashSecret.length() > 4 ?
    hashSecret.substring(hashSecret.length() - 4) : "null");

    boolean isValid = checkHash.equals(vnpSecureHash);
    log.info("Verification Result: {}", isValid);
    log.info("=============================================");

    return isValid;
    }

    public String createPaymentUrl(BigDecimal amount, String txnRef, String fullName, String email, PaymentMethod paymentMethod) {

        Map<String, String> vnpParams = new HashMap<>();

        vnpParams.put("vnp_Version", version);
        vnpParams.put("vnp_Command", command);
        vnpParams.put("vnp_TmnCode", tmnCode);

        // dùng txnRef từ ngoài
        vnpParams.put("vnp_TxnRef", txnRef);

        BigDecimal exchangeRate = BigDecimal.valueOf(25400);
        BigDecimal amountInVND = amount.multiply(exchangeRate); // 150 USD * 25400 =

        // VNPay yêu cầu số tiền gửi lên phải nhân thêm 100 lần (tức là 381,000,000)
        long vnpAmount = amountInVND.multiply(BigDecimal.valueOf(100)).longValue();
        vnpParams.put("vnp_Amount", String.valueOf(vnpAmount));
        vnpParams.put("vnp_CurrCode", currCode);

        vnpParams.put("vnp_OrderInfo", "Thanh toan ve may bay AirGo - Ma " + txnRef);
        vnpParams.put("vnp_OrderType", ortherType);

        vnpParams.put("vnp_Locale", locale);

        // Đính kèm các thông tin tùy chọn (fullName, email, paymentMethod) vào returnUrl để nhận lại khi VNPay redirect về
        String customReturnUrl = returnUrl + "?fullName=" + URLEncoder.encode(fullName != null ? fullName : "Anonymous", StandardCharsets.UTF_8)
                + "&email=" + URLEncoder.encode(email != null ? email : "no-email@example.com", StandardCharsets.UTF_8)
                + "&paymentMethod=" + (paymentMethod != null ? paymentMethod.name() : PaymentMethod.VNPAY.name());
        vnpParams.put("vnp_ReturnUrl", customReturnUrl);
        vnpParams.put("vnp_IpAddr", "127.0.0.1");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnpParams.put("vnp_CreateDate", formatter.format(new Date()));

        String query = VnPayUtil.buildQuery(vnpParams);
        String secureHash = VnPayUtil.hmacSHA512(hashSecret, query);

        String fullUrl = payUrl + "?" + query + "&vnp_SecureHash=" + secureHash;

        log.info("=== VNPay Payment URL Created ===");
        log.info("TxnRef: {}", txnRef);
        log.info("Amount: {}", amount);
        log.info("FullName: {}, Email: {}", fullName, email);
        log.info("Payment URL: {}", fullUrl);
        log.info("===================================");

        return fullUrl;
    }

    // public boolean verifyReturn(Map<String, String> params) {
    //     log.info("=== VNPay Callback Signature Verification ===");
    //     log.info("Incoming Params: {}", params);

    //     Map<String, String> verifyParams = new HashMap<>(params);
    //     String vnpSecureHash = verifyParams.remove("vnp_SecureHash");

    //     // Loại bỏ các custom parameter của chúng ta để tính checksum chính xác theo thuật toán của VNPay
    //     verifyParams.remove("fullName");
    //     verifyParams.remove("email");
    //     verifyParams.remove("paymentMethod");

    //     String signData = VnPayUtil.buildQuery(verifyParams);
        
    //     log.info("--- Sorted Params for Hashing ---");
    //     List<String> fieldNames = new ArrayList<>(verifyParams.keySet());
    //     Collections.sort(fieldNames);
    //     for (String fieldName : fieldNames) {
    //         log.info("  {} = {}", fieldName, verifyParams.get(fieldName));
    //     }
    //     log.info("---------------------------------");

    //     String checkHash = VnPayUtil.hmacSHA512(hashSecret, signData);

    //     log.info("Sign Data (Raw Query): {}", signData);
    //     log.info("Calculated Hash:      {}", checkHash);
    //     log.info("Received SecureHash:  {}", vnpSecureHash);
    //     log.info("HashSecret (Length: {}): {}...{}", 
    //             hashSecret != null ? hashSecret.length() : 0,
    //             hashSecret != null && hashSecret.length() > 4 ? hashSecret.substring(0, 4) : "null",
    //             hashSecret != null && hashSecret.length() > 4 ? hashSecret.substring(hashSecret.length() - 4) : "null");

    //     boolean isValid = checkHash.equals(vnpSecureHash);
    //     log.info("Verification Result:  {}", isValid);
    //     log.info("=============================================");

    //     return isValid;
    // }
}