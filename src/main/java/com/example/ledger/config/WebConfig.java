package com.example.ledger.config;

import com.example.ledger.web.RequestIdFilter;
import com.example.ledger.web.StatusServlet;
import javax.sql.DataSource;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<RequestIdFilter> requestIdFilter() {
        FilterRegistrationBean<RequestIdFilter> registration = new FilterRegistrationBean<>(new RequestIdFilter());
        registration.addUrlPatterns("/*");
        // Run before Spring Security so rejected requests are logged too.
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    @Bean
    public ServletRegistrationBean<StatusServlet> statusServlet(DataSource dataSource) {
        return new ServletRegistrationBean<>(new StatusServlet(dataSource), "/status");
    }
}
