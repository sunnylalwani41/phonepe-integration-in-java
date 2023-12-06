package com.phonepe.Phonepe.Payment.Gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeController {
    private final String SALTKEY = "099eb0cd-02cf-4e2a-8aca-3e6c6aff0399";
    private final String SALTINDEX = "1";
    @GetMapping("/home")
    public String home(){
        return "index";
    }
    @PostMapping("/submitData")
    @ResponseBody
    public String processTransaction(@RequestBody Map<String, Object> map) throws IOException {

        OkHttpClient client = new OkHttpClient();
        /*
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("merchantId", "PGTESTPAYUAT");
        requestBody.put("merchantTransactionId", "DA"+System.currentTimeMillis()+"On");
        requestBody.put("merchantUserId", "MUID123");
        requestBody.put("amount", Integer.parseInt((String) map.get("amount"))*100);
        requestBody.put("redirectUrl", "http://localhost:8080/successful.html");
        requestBody.put("redirectMode", "POST");
        requestBody.put("callbackUrl", "http://localhost:8080/successful.html");
        requestBody.put("mobileNumber", map.get("mobile"));
        Map<String, String> paymentInstrument = new HashMap<>();
        paymentInstrument.put("type", "PAY_PAGE");
        requestBody.put("paymentInstrument", paymentInstrument);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonObject = objectMapper.writeValueAsString(requestBody);
        */
        String transaction = String.valueOf(System.currentTimeMillis());
        String jsonObject = "{\n" +
                "  \"merchantId\": \"PGTESTPAYUAT\",\n" +
                "  \"merchantTransactionId\": \"DA"+transaction+"On\",\n" +
                "  \"merchantUserId\": \"MUID123\",\n" +
                "  \"amount\": Integer.parseInt((String) map.get(\"amount\"))*100,\n" +
                "  \"redirectUrl\": \"http://localhost:8080/successful.html\",\n" +
                "  \"redirectMode\": \"REDIRECT\",\n" +
                "  \"callbackUrl\": \"http://localhost:8080/successful.html\",\n" +
                "  \"mobileNumber\": \"9999999999\",\n" +
                "  \"paymentInstrument\": {\n" +
                "    \"type\": \"PAY_PAGE\"\n" +
                "  }\n" +
                "}";

        String payload = create64EncodedPayLoad(jsonObject);
        String mainPayload = payload+"/pg/v1/pay"+SALTKEY;
        String sha256 = calculateSHA256(mainPayload);
        String checksum = sha256+"###"+SALTINDEX;

        com.squareup.okhttp.RequestBody requestBodyChecksum = com.squareup.okhttp.RequestBody.create(MediaType.parse("application/json"), checksum);

        Request request = new Request.Builder()
                .url("https://api-preprod.phonepe.com/apis/pg-sandbox/pg/v1/pay")
                .post(requestBodyChecksum)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
        return response.body().string();
    }
    public String create64EncodedPayLoad(String payload){
        return Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }

    private String calculateSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to hexadecimal representation
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
