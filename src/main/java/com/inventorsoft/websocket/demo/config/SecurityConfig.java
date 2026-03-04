package com.inventorsoft.websocket.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
class SecurityConfig  {

    @Bean
    SecurityFilterChain securityFilterChain(final HttpSecurity http) {
        return http.authorizeHttpRequests(r -> r.anyRequest().permitAll()).build();
    }

}
