package com.phonepe.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class PaymentUtils {

    public String base64Encode(Map<String, Object> inputMap) {
        String jsonPayload = new Gson().toJson(inputMap);
        byte[] dataBytes = jsonPayload.getBytes(StandardCharsets.UTF_8);
        return Base64.encodeBase64String(dataBytes);
    }
    
    public String calculateSHA256String(String inputString) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(inputString.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();

        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }
    
    public String extractURL(String jsonResponse) {
        String url = "";

        int startIndex = jsonResponse.indexOf("url=");
        if (startIndex != -1) {
            int commaIndex = jsonResponse.indexOf(",", startIndex);

            if (commaIndex != -1) {
                url = jsonResponse.substring(startIndex + 4, commaIndex); // Adding 4 to exclude "url="
            } else {
                System.out.println("Comma after 'url=' not found.");
            }
        } else {
            System.out.println("Substring 'url=' not found in the string.");
        }

        return url;
    }
}
