package com.veritas.api.gateway.integration;

import com.veritas.api.gateway.ApiGatewayApplication;
import com.veritas.api.gateway.config.SecurityConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Integration tests for the {@link ApiGatewayApplication} class.
 */
@SpringBootTest
class ApiGatewayApplicationIT {
    
    @Autowired
    private SecurityConfig securityConfig;
    
    @Test
    void contextLoads() {
        Assertions.assertDoesNotThrow(() -> {
            ApiGatewayApplication.main(new String[]{});
        });
    }
    
    @Test
    void securityConfigLoads() {
        Assertions.assertNotNull(securityConfig);
        SecurityWebFilterChain filterChain = securityConfig.springSecurityFilterChain(new ServerHttpSecurity());
        Assertions.assertNotNull(filterChain);
    }
}
