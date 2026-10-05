package com.example.ledger.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LedgerApiTest {

    // Same value as PayloadSignerTest: the service must sign with the agreed algorithm and format.
    private static final String EXPECTED_SIGNATURE = "da0af58704897699d6e9ee3da34bea2b02bd529cef05522d59b5a7cc6b737366";

    @Autowired
    private MockMvc mvc;

    private static MockHttpServletRequestBuilder transfer(String json) {
        return post("/api/transfers").contentType(MediaType.APPLICATION_JSON).content(json);
    }

    @Test
    public void rejectsAnonymousCaller() throws Exception {
        mvc.perform(get("/api/accounts")).andExpect(status().isUnauthorized());
    }

    @Test
    public void listsAccounts() throws Exception {
        mvc.perform(get("/api/accounts").with(httpBasic("auditor", "auditor123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].accountNumber").value("0123456789"));
    }

    @Test
    public void getsOneAccount() throws Exception {
        mvc.perform(get("/api/accounts/9876543210").with(httpBasic("teller", "teller123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holderName").value("Budi Santoso"))
                .andExpect(jsonPath("$.balance").value(1250000.00));
    }

    @Test
    public void unknownAccountIs404() throws Exception {
        mvc.perform(get("/api/accounts/1111111111").with(httpBasic("teller", "teller123")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("ACCOUNT_NOT_FOUND"));
    }

    @Test
    public void transferMovesMoneyAndIsSigned() throws Exception {
        mvc.perform(transfer("{\"fromAccount\":\"0123456789\",\"toAccount\":\"9876543210\","
                        + "\"amount\":250000,\"reference\":\"INV-2026-001\"}")
                        .with(httpBasic("teller", "teller123")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.amount").value(250000.00))
                .andExpect(jsonPath("$.signature").value(EXPECTED_SIGNATURE));

        mvc.perform(get("/api/accounts/0123456789").with(httpBasic("teller", "teller123")))
                .andExpect(jsonPath("$.balance").value(4750000.00));
        mvc.perform(get("/api/accounts/9876543210").with(httpBasic("teller", "teller123")))
                .andExpect(jsonPath("$.balance").value(1500000.00));
    }

    @Test
    public void auditorCannotTransfer() throws Exception {
        mvc.perform(transfer("{\"fromAccount\":\"0123456789\",\"toAccount\":\"9876543210\",\"amount\":10}")
                        .with(httpBasic("auditor", "auditor123")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void invalidTransferListsFields() throws Exception {
        mvc.perform(transfer("{\"fromAccount\":\"12345\",\"amount\":0.001,"
                        + "\"reference\":\"this reference is far too long for the clearing format\"}")
                        .with(httpBasic("teller", "teller123")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fields.fromAccount").exists())
                .andExpect(jsonPath("$.fields.toAccount").exists())
                .andExpect(jsonPath("$.fields.amount").exists())
                .andExpect(jsonPath("$.fields.reference").exists());
    }

    @Test
    public void insufficientFundsIsRejected() throws Exception {
        mvc.perform(transfer("{\"fromAccount\":\"5550001112\",\"toAccount\":\"0123456789\",\"amount\":1}")
                        .with(httpBasic("teller", "teller123")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("INSUFFICIENT_FUNDS"));
    }

    @Test
    public void echoesCallerRequestId() throws Exception {
        mvc.perform(get("/api/accounts").header(RequestIdFilter.HEADER, "abc-123")
                        .with(httpBasic("teller", "teller123")))
                .andExpect(header().string(RequestIdFilter.HEADER, "abc-123"));
    }

    @Test
    public void generatesRequestIdWhenMissing() throws Exception {
        mvc.perform(get("/api/accounts"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(RequestIdFilter.HEADER, matchesPattern("[0-9a-f-]{36}")));
    }
}
