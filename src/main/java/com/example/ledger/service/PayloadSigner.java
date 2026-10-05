package com.example.ledger.service;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Signs transfer payloads with HMAC-SHA256 so the downstream clearing system can
 * check that nothing changed in transit.
 */
@Component
public class PayloadSigner {

    private static final String ALGORITHM = "HmacSHA256";

    private final String key;
    private SecretKeySpec keySpec;

    public PayloadSigner(@Value("${ledger.signing.key}") String key) {
        this.key = key;
    }

    @PostConstruct
    void init() {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("ledger.signing.key must be at least 32 bytes");
        }
        keySpec = new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public String sign(String payload) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(keySpec);
            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Cannot sign payload", e);
        }
    }
}
