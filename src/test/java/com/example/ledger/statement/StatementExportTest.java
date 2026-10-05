package com.example.ledger.statement;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class StatementExportTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void exportsStatementAsXml() throws Exception {
        mvc.perform(post("/api/transfers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromAccount\":\"0123456789\",\"toAccount\":\"5550001112\","
                                + "\"amount\":75000.50,\"reference\":\"QRIS-778\"}")
                        .with(httpBasic("teller", "teller123")))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/accounts/5550001112/statement").with(httpBasic("auditor", "auditor123")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
                .andExpect(xpath("/statement/@account").string("5550001112"))
                .andExpect(xpath("/statement/@currency").string("IDR"))
                .andExpect(xpath("/statement/holder").string("Toko Maju Jaya"))
                .andExpect(xpath("/statement/closingBalance").string("75000.50"))
                .andExpect(xpath("/statement/lines/line").nodeCount(1))
                .andExpect(xpath("/statement/lines/line[1]/@direction").string("CREDIT"))
                .andExpect(xpath("/statement/lines/line[1]/counterparty").string("0123456789"))
                .andExpect(xpath("/statement/lines/line[1]/amount").string("75000.50"))
                .andExpect(xpath("/statement/lines/line[1]/reference").string("QRIS-778"));
    }

    @Test
    public void emptyStatementHasNoLines() throws Exception {
        mvc.perform(get("/api/accounts/9876543210/statement").with(httpBasic("auditor", "auditor123")))
                .andExpect(status().isOk())
                .andExpect(xpath("/statement/closingBalance").string("1250000.00"))
                .andExpect(xpath("/statement/lines/line").nodeCount(0));
    }
}
