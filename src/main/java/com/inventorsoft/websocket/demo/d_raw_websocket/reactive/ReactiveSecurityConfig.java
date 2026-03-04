package com.inventorsoft.websocket.demo.d_raw_websocket.reactive;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration(proxyBeanMethods = false)
class ReactiveSecurityConfig {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(final ServerHttpSecurity httpSecurity) {
        return httpSecurity.authorizeExchange(s -> s.pathMatchers("/**").permitAll()).build();
    }

}
