package com.veritas.api.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuration class for security settings using OAuth2 resource server.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  /**
   * Configures the security filters and rules for the server.
   *
   * @param http the ServerHttpSecurity object to configure
   * @return the configured SecurityWebFilterChain object
   */
  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http
        .csrf().disable()
        .authorizeExchange(exchange -> exchange
            .pathMatchers("/eureka/**", "/discovery-server/**").permitAll()
            .anyExchange().authenticated())
        .oauth2ResourceServer()
        .jwt();
    
    return http.build();
  }
}
