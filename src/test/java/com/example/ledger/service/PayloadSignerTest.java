package com.example.ledger.service;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;

public class PayloadSignerTest {

    static final String LAB_KEY = "lab-signing-key-not-for-production-use";
    // Known-answer value agreed with the clearing system. It must never change.
    static final String EXPECTED_SIGNATURE = "da0af58704897699d6e9ee3da34bea2b02bd529cef05522d59b5a7cc6b737366";

    private PayloadSigner signer;

    @Before
    public void setUp() {
        signer = new PayloadSigner(LAB_KEY);
        signer.init();
    }

    @Test
    public void canonicalPayloadFormat() {
        assertEquals("0123456789|9876543210|250000.00|INV-2026-001",
                TransferService.canonicalPayload("0123456789", "9876543210", new BigDecimal("250000.00"), "INV-2026-001"));
    }

    @Test
    public void signatureMatchesKnownAnswer() {
        assertEquals(EXPECTED_SIGNATURE, signer.sign("0123456789|9876543210|250000.00|INV-2026-001"));
    }

    @Test(expected = IllegalStateException.class)
    public void rejectsShortKey() {
        new PayloadSigner("too-short").init();
    }
}
