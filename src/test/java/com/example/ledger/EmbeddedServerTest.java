package com.example.ledger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/** Boots the real embedded Tomcat so the servlet, the filter and basic auth are exercised end to end. */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmbeddedServerTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    public void statusServletIsPublic() {
        ResponseEntity<String> response = rest.getForEntity("/status", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("UP", response.getBody());
        assertNotNull(response.getHeaders().getFirst("X-Request-Id"));
    }

    @Test
    public void accountsNeedBasicAuth() {
        assertEquals(HttpStatus.UNAUTHORIZED, rest.getForEntity("/api/accounts", String.class).getStatusCode());
        ResponseEntity<String> response = rest.withBasicAuth("auditor", "auditor123")
                .getForEntity("/api/accounts/0123456789", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
