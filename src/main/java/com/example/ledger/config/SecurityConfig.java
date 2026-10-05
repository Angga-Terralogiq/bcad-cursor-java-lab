package com.example.ledger.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Lab users only. A real service authenticates against the bank's identity provider.
        auth.inMemoryAuthentication()
                .withUser("teller").password("{noop}teller123").roles("TELLER")
                .and()
                .withUser("auditor").password("{noop}auditor123").roles("AUDITOR");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/status", "/error").permitAll()
                .antMatchers(HttpMethod.POST, "/api/transfers").hasRole("TELLER")
                .antMatchers("/api/**").hasAnyRole("TELLER", "AUDITOR")
                .anyRequest().denyAll()
                .and()
                .httpBasic();
    }
}
