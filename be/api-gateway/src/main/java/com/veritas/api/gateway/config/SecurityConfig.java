package com.veritas.api.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

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
        .cors().and()
        .authorizeExchange(exchange -> exchange
            .pathMatchers("/eureka/**", "/discovery-server/**").permitAll()
            .anyExchange().authenticated())
        .oauth2ResourceServer()
        .jwt();

    return http.build();
  }

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE) // Crucial: CORS filter *before* security filters
  public CorsWebFilter corsWebFilter() {
    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.addAllowedOrigin("http://localhost:5173");
    corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    corsConfig.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    corsConfig.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);

    return new CorsWebFilter(source);
  }
}
