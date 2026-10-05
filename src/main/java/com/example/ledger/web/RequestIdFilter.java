package com.example.ledger.web;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Gives every request an ID (taken from the caller or generated), echoes it back
 * and writes one audit line per request.
 */
public class RequestIdFilter implements Filter {

    public static final String HEADER = "X-Request-Id";
    private static final Logger AUDIT = LoggerFactory.getLogger("audit");

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String requestId = request.getHeader(HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        response.setHeader(HEADER, requestId);
        MDC.put("requestId", requestId);
        long start = System.nanoTime();
        try {
            chain.doFilter(request, response);
        } finally {
            long millis = (System.nanoTime() - start) / 1_000_000;
            AUDIT.info("{} {} -> {} ({} ms)", request.getMethod(), request.getRequestURI(), response.getStatus(), millis);
            MDC.remove("requestId");
        }
    }
}
