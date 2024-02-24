package com.phonepe.service;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import com.phonepe.utils.PaymentUtils;

@Service
public class PaymentService {

    @Autowired
    private PaymentUtils paymentUtils;

    @Value("${payment.endpoint}")
    private String endpoint;

    @Value("${payment.saltkey}")
    private String saltKey;
    
    @Value("${pay.url}")
    private String payUrl;

    public RedirectView initiatePayment() throws NoSuchAlgorithmException {
        String ENDPOINT = "/pg/v1/pay";
        String merchantTransactionId = java.util.UUID.
        		randomUUID
        		().
        		toString
        		();
        Map<String, Object> mainPayload = new HashMap<>();
        mainPayload.put("merchantId", "PGTESTPAYUAT");
        mainPayload.put("merchantTransactionId", "MT7850590068188104");
        mainPayload.put("merchantUserId", "MUID123");
        mainPayload.put("amount", 10000);
        mainPayload.put("redirectUrl", "http://127.0.0.1:8080/paymentsuccess");
        mainPayload.put("redirectMode", "POST");
        mainPayload.put("callbackUrl", "http://127.0.0.1:8080/paymentsuccess");
        mainPayload.put("mobileNumber", "9999999999");

        Map<String, String> paymentInstrument = new HashMap<>();
        paymentInstrument.put("type", "PAY_PAGE");
        mainPayload.put("paymentInstrument", paymentInstrument);

        String base64String = paymentUtils.base64Encode(mainPayload);
        String mainString = base64String + ENDPOINT + saltKey;
        String sha256Val = paymentUtils.calculateSHA256String(mainString);
        String checkSum = sha256Val + "###1";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-VERIFY", checkSum);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, String> jsonPayload = new HashMap<>();
        jsonPayload.put("request", base64String);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(jsonPayload, headers);
        
        System.out.println("mllllllllll---------"+ requestEntity);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> responseEntity = restTemplate.postForEntity(payUrl, requestEntity, Object.class);

        System.out.println("mkmkm---------"+ responseEntity);
        String jsonResponse = responseEntity.getBody().toString();
        String url=paymentUtils.extractURL(jsonResponse);
		return new RedirectView(url);
    }

    public String handlePaymentReturn(Map<String, String> formParams) throws NoSuchAlgorithmException {
        String INDEX = "1";
        String transactionId = formParams.get("transactionId");
        String jsonResponse="";
        if (transactionId != null) {
            String requestUrl = "https://api-preprod.phonepe.com/apis/pg-sandbox/pg/v1/status/PGTESTPAYUAT/" + transactionId;
            String sha256PayloadString = "/pg/v1/status/PGTESTPAYUAT/" + transactionId + saltKey;
            String sha256Val = paymentUtils.calculateSHA256String(sha256PayloadString);
            String checksum = sha256Val + "###" + INDEX;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-VERIFY", checksum);
            headers.set("X-MERCHANT-ID", transactionId);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Object> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.GET, requestEntity, Object.class);
            jsonResponse = responseEntity.getBody().toString();
        }
        return jsonResponse;
    }
}
